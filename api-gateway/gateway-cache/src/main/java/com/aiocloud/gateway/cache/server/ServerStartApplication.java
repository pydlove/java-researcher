package com.aiocloud.gateway.cache.server;

/**
 *
 * @description: ServerStartApplication.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-06 18:29
 */
@CacheBootApplication
public class ServerStartApplication {

    /**
     * 服务端启动入口方法
     *
     * @param: args
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-06 18:22
     * @since 1.0.0
     */
    public static void main(String[] args) {
        CacheServerApplication.run(ServerStartApplication.class, args);
    }
}
