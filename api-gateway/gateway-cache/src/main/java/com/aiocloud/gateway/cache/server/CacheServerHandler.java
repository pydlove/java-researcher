package com.aiocloud.gateway.cache.server;

import com.aiocloud.gateway.cache.client.protocol.*;
import com.aiocloud.gateway.cache.client.resolver.MessageResolver;
import com.aiocloud.gateway.cache.client.resolver.MessageResolverFactory;
import com.aiocloud.gateway.cache.client.resolver.RequestMessageResolver;
import com.aiocloud.gateway.cache.client.resolver.ResponseMessageResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 *
 * @description: CacheServerHandler.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-01-03 17:44 
 */
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
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        // 注册 request 消息处理器
        resolverFactory.registerResolver(new RequestMessageResolver());
    }
}
