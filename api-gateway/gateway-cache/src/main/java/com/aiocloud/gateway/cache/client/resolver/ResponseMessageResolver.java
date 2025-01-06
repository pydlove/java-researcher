package com.aiocloud.gateway.cache.client.resolver;

import com.aiocloud.gateway.cache.client.protocol.Message;
import com.aiocloud.gateway.cache.client.protocol.MessageTypeEnum;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @description: ResponseMessageResolver.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-01-06 14:57 
 */
public class ResponseMessageResolver implements MessageResolver {

    private static final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public boolean support(Message message) {
        return message.getMessageType() == MessageTypeEnum.RESPONSE;
    }

    @Override
    public Message resolve(Message message) {

        Message empty = new Message();
        empty.setMessageType(MessageTypeEnum.EMPTY);
        return empty;
    }
}
