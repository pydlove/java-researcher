package com.aiocloud.gateway.cache.client.resolver;

import com.aiocloud.gateway.cache.client.protocol.Message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description: MessageResolverFactory.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-06 14:42
 */
public class MessageResolverFactory {

    private static final List<MessageResolver> resolvers = new CopyOnWriteArrayList<>();

    private static class SingletonHolder {
        private static final MessageResolverFactory INSTANCE = new MessageResolverFactory();
    }

    public static final MessageResolverFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private MessageResolverFactory() {
    }

    public void registerResolver(MessageResolver resolver) {
        resolvers.add(resolver);
    }

    /**
     * 根据解码后的消息，在工厂类处理器中查找可以处理当前消息的处理器
     *
     * @param: message
     * @return: com.aiocloud.gateway.cache.client.resolver.MessageResolver
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-06 14:52
     * @since 1.0.0
     */
    public MessageResolver getMessageResolver(Message message) {

        for (MessageResolver resolver : resolvers) {
            if (resolver.support(message)) {
                return resolver;
            }
        }

        throw new RuntimeException("cannot find resolver, message type: " + message.getMessageType());
    }
}
