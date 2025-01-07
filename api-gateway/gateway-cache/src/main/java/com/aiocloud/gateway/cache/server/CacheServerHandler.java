package com.aiocloud.gateway.cache.server;

import com.aiocloud.gateway.cache.server.protocol.Message;
import com.aiocloud.gateway.cache.server.resolver.MessageResolver;
import com.aiocloud.gateway.cache.server.resolver.MessageResolverFactory;
import com.aiocloud.gateway.cache.server.resolver.RequestGetMessageResolver;
import com.aiocloud.gateway.cache.server.resolver.RequestSetMessageResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @description: CacheServerHandler.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-01-03 17:44 
 */
@Slf4j
public class CacheServerHandler extends SimpleChannelInboundHandler<Message> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取一个消息处理器工厂类实例
     */
    private MessageResolverFactory resolverFactory = MessageResolverFactory.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        // 获取消息处理器
        MessageResolver resolver = resolverFactory.getMessageResolver(message);

        // 对消息进行处理并获取响应数据
        Message result = resolver.resolve(message);

        // 将响应数据写入到处理器中
        ctx.writeAndFlush(result);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        log.error("Encountered some errors, caused by:", cause);
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        // 注册获取缓存的消息处理器
        resolverFactory.registerResolver(new RequestGetMessageResolver());

        // 注册设置缓存的消息处理器
        resolverFactory.registerResolver(new RequestSetMessageResolver());
    }
}
