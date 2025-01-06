package com.aiocloud.gateway.cache.server;

import com.aiocloud.gateway.cache.conf.SystemProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: CacheServerApplication.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-06 18:24
 */
@Slf4j
public class CacheServerApplication {

    /**
     * 启动服务
     *
     * @param: serverMainClass
     * @param: args
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-06 18:32
     * @since 1.0.0
     */
    public static void run(Class<ServerStartApplication> serverMainClass, String[] args) {

        try {

            Class<?> mainClass = serverMainClass;
            if (mainClass.isAnnotationPresent(CacheBootApplication.class)) {

                int port = SystemProperties.serverPort;
                if (args.length > 0) {
                    port = Integer.parseInt(args[0]);
                }

                // 这里可以实现服务启动之前需要准备的逻辑
                new CacheServer(port).run();

            } else {
                log.error("Main class is not annotated with @CacheBootApplication");
            }

        } catch (Exception ex) {
            log.error("Error starting server, caused by:", ex);
        }
    }
}
