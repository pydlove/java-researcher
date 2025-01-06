package com.aiocloud.gateway.cache.client;

import cn.hutool.core.util.StrUtil;
import com.aiocloud.gateway.cache.client.protocol.Message;
import com.aiocloud.gateway.cache.client.protocol.MessageTypeEnum;
import com.aiocloud.gateway.cache.model.CacheMessage;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: CacheClientHandler.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-03 17:47
 */
public class CacheClientHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(CacheClient.class);

    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        this.ctx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        String key = null;
        String responseMessage = null;

        try {

            // 这里是消息的返回
            String body = message.getBody();
            if (StrUtil.isNotEmpty(body)) {

                CacheMessage cacheMessage = JSONObject.parseObject(body, CacheMessage.class);
                key = cacheMessage.getKey();
                responseMessage = cacheMessage.getResponseMessage();

                log.debug("set cache success, key: {}, responseMessage: {}", key, responseMessage);
            }


        } catch (Exception ex) {
            log.error("set cache error, key: {}, responseMessage: {}, cause by:", key, responseMessage, ex);
        }

        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    /**
     * 发送消息
     *
     * @param: key
     * @param: value
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-06 18:15
     * @since 1.0.0
     */
    public void sendMessage(String key, Object value) {

        // 这里要通过 LV 的协议来发送消息
        ByteBuf buffer = Unpooled.copiedBuffer(key, CharsetUtil.UTF_8);
        CacheMessage cacheMessage = new CacheMessage();
        cacheMessage.setKey(key);
        cacheMessage.setValue(value);

        Message message = new Message();
        message.setMessageType(MessageTypeEnum.REQUEST);
        message.setBody(JSONObject.toJSONString(cacheMessage));
        this.ctx.writeAndFlush(message);
    }
}