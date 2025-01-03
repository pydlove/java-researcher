## 功能

1. 路由转发
2. 服务注册
3. 服务发现
4. 认证与授权

---

## 应用的技术

### JWT 认证

JWT 有什么问题？目前发现 JWT 无法实现续期，实现续期需要结合方案：
1. 短生命周期令牌 + 刷新令牌; （我们主要实现这种方案）
2. 自动续期（这个依赖客户端，虽然简单，但是开销大，所有一般不这么做）

### 短生命周期令牌 + 刷新令牌如何实现？

短生命周期令牌 `accessToken` 默认 lifetime 为 30 分钟，刷新令牌 `refreshToken` lifetime 为 7 天。
每次访问将 `accessToken` 传给服务端，服务端校验 `accessToken` 的有效性，如果过期的话，
就将 `refreshToken` 传给服务端。如果 `refreshToken` 有效，服务端就生成新的 `accessToken` 给客户端。
否则，客户端就重新登录即可。

### 如何进行 token 的验证？

`com.aiocloud.gateway.router.config.TokenCheck` 类主要负责 token 的校验，主要校验步骤如下：
1. 对一些接口进行放行，支持白名单、黑名单
2. token 签名验证
3. 过期时间检查
4. Issuer (iss) 和 Audience (aud) 检查
5. 用户是否有权限访问接口（这个是后续扩展）

[com.aiocloud.gateway.router.config.TokenCheck](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-center/src/main/java/com/aiocloud/gateway/router/config/TokenCheck.java)

### AntPathMatcher 实现路径的匹配判断

在做黑白名单判断时，使用了 `AntPathMatcher`，这个类是 Spring 提供的，
可以通过 `AntPathMatcher` 的 `match` 方法进行路径的匹配判断。`AntPathMatcher` 的底层是通过将 URL
按 `/` 一层一层分割后，对两个数组相同位置做匹配的，`**` 是直接不继续判断后续的匹配。

[com.aiocloud.gateway.router.access.WhitelistFilter](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-center/src/main/java/com/aiocloud/gateway/router/access/WhitelistFilter.java)  
[com.aiocloud.gateway.router.access.Blacklist](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-center/src/main/java/com/aiocloud/gateway/router/access/Blacklist.java)

### 数据库连接池 druid

基于此数据源连接池，配置文件如何实现加密？
通过工具类 `com.aiocloud.gateway.base.utils.DruidPasswordEncryptorUtil` 进行加密，
在加载 DataSource 的时候，对用户密码进行解密。

[com.aiocloud.gateway.base.utils.DruidPasswordEncryptorUtil](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-center/src/main/java/com/aiocloud/gateway/base/utils/DruidPasswordEncryptorUtil.java)

### 责任链的设计模式

在 `com.aiocloud.gateway.router.access` 包下面，是责任链的模式，主要解决的业务场景是需要对访问的请求
进行黑名单、白名单过滤。这里还设计了执行的优先权重，通过 `com.aiocloud.gateway.router.access.FilterOrder`
这个注解实现了过滤的优先级，黑名单的优先级要高于白名单。

[com.aiocloud.gateway.router.access.FilterOrder](https://github.com/pydlove/java-researcher/blob/main/api-gateway/gateway-center/src/main/java/com/aiocloud/gateway/router/access/FilterOrder.java)

### 如何实现系统缓存？

写到这里，我发现系统里有很多的地方需要缓存，比如路由、服务、黑名单、白名单、用户信息等。内存的选型方案我们有很多种，
大体可以分成两类，一类是本地缓存，一类是异地缓存。本地缓存大家可能用过很多，例如 map、list、set，还有框架级别的缓存
Caffeine 等，异地缓存就很多了，例如 redis、memcached 等，异地主要是用来实现分布式使用。  
这里我们出于学习的目的，我们玩个骚操作，我们基于 Caffine 实现一个异地缓存。
