## 功能

1. 路由转发
2. 服务注册
3. 服务发现
4. 认证与授权

## 应用的技术
JWT 认证：

jwt有什么问题？目前发现 JWT 无法实现续期，实现续期需要结合方案：
1. 短生命周期令牌 + 刷新令牌; （我们主要实现这种方案）；
2. 自动续期（这个依赖客户端，虽然简单，但是开销大，所有一般不这么做）；

短生命周期令牌 + 刷新令牌如何实现呢？

短生命周期令牌 accessToken 默认lifetime为30分钟，刷新令牌 refreshToken lifetime为7天。
每次访问将 accessToken 传给服务端，服务端校验 accessToken 的有效性，如果过期的话，
就将 refreshToken 传给服务端。如果 refreshToken 有效，服务端就生成新的 accessToken 给客户端。
否则，客户端就重新登录即可。


数据库连接池 druid：

基于此数据源连接池，配置文件如何实现加密？
通过工具类 com.aiocloud.gateway.base.utils.DruidPasswordEncryptorUtil 进行加密，
在加载 DataSource 的时候，对用户密码进行解密。


