package com.aiocloud.gateway.cache.server.resolver;

import com.aiocloud.gateway.cache.server.protocol.Message;

/**
 *
 * @description: MessageResolver.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-01-06 14:52 
 */
public interface MessageResolver {

    boolean support(Message message);

    Message resolve(Message message);
}
