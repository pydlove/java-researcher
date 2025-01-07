package com.aiocloud.gateway.cache.server.protocol;

import java.util.UUID;

/**
 *
 * @description: SessionIdGenerator.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-01-06 14:06 
 */
public class SessionIdGenerator {

    public static String generateSessionId() {
        return UUID.randomUUID().toString();
    }

}
