# API Gateway 项目说明

## 目录

- [功能](#功能)
- [项目结构](#项目结构)
- [应用的技术](#应用的技术)
    - [JWT 认证](#jwt-认证)
        - [短生命周期令牌 + 刷新令牌如何实现？](#短生命周期令牌-刷新令牌如何实现)
        - [如何进行 token 的验证？](#如何进行-token-的验证)
    - [AntPathMatcher 实现路径的匹配判断](#antpathmatcher-实现路径的匹配判断)
    - [数据库连接池 Druid](#数据库连接池-druid)
    - [责任链的设计模式](#责任链的设计模式)
    - [系统缓存的实现](#系统缓存的实现)
        - [缓存的数据存储在哪里？](#缓存的数据存储在哪里)
        - [如何设置和读取缓存？](#如何设置和读取缓存)
    - [配置文件信息读取](#配置文件信息读取)
    - [LV 协议](#lv-协议)
    - [客户端如何连接缓存服务端？](#客户端如何连接缓存服务端)
    - [缓存服务端如何启动？](#缓存服务端如何启动)
    - [缓存客户端如何设置和读取缓存？](#缓存客户端如何设置和读取缓存)
    - [并发情况的 message id 如何生成？](#并发情况的-message-id-如何生成)

---

## 功能

1. 路由转发
2. 服务注册
3. 服务发现
4. 认证与授权

---

## 项目结构

api-gateway   
├── dao-mysql  持久层操作  
├── gateway-cache  缓存   
├── gateway-center 注册中心  
├── gateway-core 网关核心包  

---

## 应用的技术

### JWT 认证

JWT 有什么问题？目前发现 JWT 无法实现续期，实现续期需要结合方案：

1. 短生命周期令牌 + 刷新令牌; （我们主要实现这种方案）
2. 自动续期（这个依赖客户端，虽然简单，但是开销大，所有一般不这么做）

#### 短生命周期令牌 + 刷新令牌如何实现？

短生命周期令牌 `accessToken` 默认 lifetime 为 30 分钟，刷新令牌 `refreshToken` lifetime 为 7 天。
每次访问将 `accessToken` 传给服务端，服务端校验 `accessToken` 的有效性，如果过期的话，
就将 `refreshToken` 传给服务端。如果 `refreshToken` 有效，服务端就生成新的 `accessToken` 给客户端。
否则，客户端就重新登录即可。

#### 如何进行 token 的验证？

[`com.aiocloud.gateway.router.config.TokenCheck`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-center/src/main/java/com/aiocloud/gateway/router/config/TokenCheck.java)
类主要负责 token 的校验，主要校验步骤如下：

1. 对一些接口进行放行，支持白名单、黑名单
2. token 签名验证
3. 过期时间检查
4. Issuer (iss) 和 Audience (aud) 检查
5. 用户是否有权限访问接口（这个是后续扩展）

### AntPathMatcher 实现路径的匹配判断

在做黑白名单判断时，使用了 Spring
提供的 [`AntPathMatcher`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html)
，可以通过 `match` 方法进行路径的匹配判断。`AntPathMatcher` 的底层是通过将 URL 按 `/`
一层一层分割后，对两个数组相同位置做匹配的，`**` 是直接不继续判断后续的匹配。

相关类：

- [`com.aiocloud.gateway.router.access.WhitelistFilter`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-center/src/main/java/com/aiocloud/gateway/router/access/WhitelistFilter.java)
- [`com.aiocloud.gateway.router.access.Blacklist`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-center/src/main/java/com/aiocloud/gateway/router/access/Blacklist.java)

### 数据库连接池 Druid

基于此数据源连接池，配置文件如何实现加密？
通过工具类 [`com.aiocloud.gateway.base.utils.DruidPasswordEncryptorUtil`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-center/src/main/java/com/aiocloud/gateway/base/utils/DruidPasswordEncryptorUtil.java)
进行加密，
在加载 DataSource 的时候，对用户密码进行解密。

### 责任链的设计模式

在 `com.aiocloud.gateway.router.access` 包下面，是责任链的模式，主要解决的业务场景是需要对访问的请求
进行黑名单、白名单过滤。这里还设计了执行的优先权重，通过 [`com.aiocloud.gateway.router.access.FilterOrder`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-center/src/main/java/com/aiocloud/gateway/router/access/FilterOrder.java)
这个注解实现了过滤的优先级，黑名单的优先级要高于白名单。

### 如何实现系统缓存？

写到这里，我发现系统里有很多的地方需要缓存，比如路由、服务、黑名单、白名单、用户信息等。内存的选型方案我们有很多种，
大体可以分成两类，一类是本地缓存，一类是异地缓存。本地缓存大家可能用过很多，例如 map、list、set，还有框架级别的缓存
Caffeine 等，异地缓存就很多了，例如 redis、memcached 等，异地主要是用来实现分布式使用。  
这里我们出于学习的目的，我们玩个骚操作，我们基于 Caffeine 实现一个异地缓存。  
我们先梳理下实现一个分布式缓存需要哪些功能：

1. 缓存的数据存储在哪里？这里很明确我们使用的是 Caffeine，所以数据存储在 Caffeine 中。
   到时候我们就全部写在这个包里面 `com.aiocloud.gateway.cache.core`。
2. 如何设置和读取缓存？因为是远程设置缓存，我们需要一个服务，这里我们使用 netty 实现。
   服务端我们写在这个包里 `com.aiocloud.gateway.cache.server`
   ，客户端我们写在这个包里 `com.aiocloud.gateway.cache.client`。

### 我们该如何读取配置文件信息？

因为分布式的缓存它肯定需要设置很多配置参数，我们需要构建一个统一的配置参数读取的功能，这个功能写在这个包下面 `com.aiocloud.gateway.cache.conf`，
通过注解 `@Prop` 和 `ConfigLoader` 实现，我们可用通过 `SystemProperties.serverPort` 的方式读取参数，`@Prop` 设置参数。

三个核心类

- [`com.aiocloud.gateway.cache.conf.ConfigLoader`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-cache/src/main/java/com/aiocloud/gateway/cache/conf/ConfigLoader.java)
- [`com.aiocloud.gateway.cache.conf.Prop`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-cache/src/main/java/com/aiocloud/gateway/cache/conf/Prop.java)
- [`com.aiocloud.gateway.cache.conf.SystemProperties`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-cache/src/main/java/com/aiocloud/gateway/cache/conf/SystemProperties.java)

### 什么是 LV 协议？

我们通过 netty 实现了客户端和服务端，我们可以通过客户端远程进行设置缓存和读取缓存，netty 客户端和服务端中间的消息交互使用的是
LV 协议。
LV (Length Value) 协议，它有 4 个字节的 length 和 value，我们通过 netty 的 ByteBuf 来实现。

消息对象：
[`com.aiocloud.gateway.cache.server.protocol.Message`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-cache/src/main/java/com/aiocloud/gateway/cache/server/protocol/Message.java)  
LV 协议编码类：
[`com.aiocloud.gateway.cache.server.protocol.MessageEncoder`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-cache/src/main/java/com/aiocloud/gateway/cache/server/protocol/MessageEncoder.java)  
LV 协议解码类：
[`com.aiocloud.gateway.cache.server.protocol.MessageDecoder`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-cache/src/main/java/com/aiocloud/gateway/cache/server/protocol/MessageDecoder.java)

### 客户端如何连接缓存服务端？这个连接如何复用？

写到这里，我们大致实现了客户端去连接服务端并且通过调用 [`com.aiocloud.gateway.cache.client.CacheClient.setCache`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-cache/src/main/java/com/aiocloud/gateway/cache/client/CacheClient.java)
方法可以设置成功缓存，
但是有一个问题：我们每次设置缓存都要新创建一个客户端吗？创建的过程是需要连接服务端的，这个连接过程是缓慢，如果每次都新建一个，那么这个性能太差了，
我们是无法接收的，那么现在该怎么办呢？  
针对这种情况，我们大部分的设计都是使用连接池，用于复用这个连接。那么我们该如何实现这个连接池呢？

1. 使用 commons-pool 实现连接池； 核心类：

- [`com.aiocloud.gateway.cache.client.pool.CacheClientPool`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-cache/src/main/java/com/aiocloud/gateway/cache/client/pool/CacheClientPool.java)

2. 当然大家也可以自己去实现一个针对这场景的资源池，原理也很简单，实现两个方面：
   对象池化和资源管理（对象的创建、验证和销毁等）

### 缓存服务端的如何启动？

服务端可以使用 main 方法启动，我们这里可以做个有意思的东西，我们仿造 SpringBoot 来实现启动
缓存服务端的启动，我们也写一个注解 `CacheBootApplication`，我们通过这个注解来启动服务端，这样我们只需要在启动类上加上这个注解，
ServerStartApplication 这个是入口类，CacheServerApplication 这个是服务器启动的编排的类，这里面我们实现启动前、启动以及启动后
的一些要做的事情。最后通过 `com.aiocloud.gateway.cache.server.ServerStartApplication` 就可以启动服务端。

核心类：

- [`com.aiocloud.gateway.cache.server.CacheBootApplication`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-cache/src/main/java/com/aiocloud/gateway/cache/server/CacheBootApplication.java)
- [`com.aiocloud.gateway.cache.server.CacheServerApplication`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-cache/src/main/java/com/aiocloud/gateway/cache/server/CacheServerApplication.java)
- [`com.aiocloud.gateway.cache.server.CacheServer`](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-cache/src/main/java/com/aiocloud/gateway/cache/server/CacheServer.java)

### 缓存客户端如何如何设置缓存？读取缓存？

这里可以参考单元测试的代码 `com.aiocloud.gateway.cache.client.pool.CacheClientManagerTest` ，`com.aiocloud.gateway.cache.client.pool.CacheClientManager`
 是一个单例的类，通过它就可以设置缓存了。其中的原理就是：CacheClientManager 被 new 的时候会去初始化一个连接池，这个池子用于连接缓存服务端，
然后调用 setCache 方法时，客户端会发送一条消息给服务端，这条消息的类型是 REQUEST_SET，会被这个类去处理 `com.aiocloud.gateway.cache.server.resolver.RequestSetMessageResolver`
，这个类里会调用缓存的管理类 `com.aiocloud.gateway.cache.core.CacheManager` 进行存储缓存。  
同理读取缓存就是发送 REQUEST_GET 的消息，然后被 `com.aiocloud.gateway.cache.server.resolver.RequestGetMessageResolver` 处理，有所不同的是，
读取缓存时，会返回一个结果，这个结果是缓存的值，而客户端需要等待结果，这个过程是阻塞，通过的是 CompletableFuture<Message> completableFuture 的 get 方法实现，
这里有个细节的是 completableFuture 是维护在 ConcurrentMap 里面的，通过 key 来获取对应的 completableFuture，这个 key 是 message id，id 是通过
雪花算法实现的。

### 并发情况的 message id 如何生成？

这个简单点就用 AtomicLong 来生成，但是考虑以后分布式等情况，我们可以引入分布式 ID 生成器，比如：雪花算法。
我这里就用雪花算法来生成 message id。雪花算法使用的是工具包 cn.ipokerface/snowflake-id-generator，工具类是 `com.aiocloud.gateway.cache.base.utils.SnowflakeIdGeneratorUtil`。


