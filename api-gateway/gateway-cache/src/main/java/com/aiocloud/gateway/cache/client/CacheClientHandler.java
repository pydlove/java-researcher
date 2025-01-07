package com.aiocloud.gateway.cache.client;

import cn.hutool.core.util.StrUtil;
import com.aiocloud.gateway.cache.base.utils.SnowflakeIdGeneratorUtil;
import com.aiocloud.gateway.cache.server.protocol.Message;
import com.aiocloud.gateway.cache.server.protocol.MessageTypeEnum;
import com.aiocloud.gateway.cache.model.CacheMessage;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

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

    private final ConcurrentMap<Long, CompletableFuture<Message>> pendingResponses;

    public CacheClientHandler() {
        this.pendingResponses = new ConcurrentHashMap<>();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    /**
     * 处理消息的返回
     *
     * @param: ctx
     * @param: message
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-07 11:31
     * @since 1.0.0
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        String key = null;
        String body = null;
        String responseMessage = null;
        Long id = message.getId();
        MessageTypeEnum messageType;

        try {

            body = message.getBody();
            if (StrUtil.isNotEmpty(body)) {

                CacheMessage cacheMessage = JSONObject.parseObject(body, CacheMessage.class);
                key = cacheMessage.getKey();
                responseMessage = cacheMessage.getResponseMessage();
                messageType = message.getMessageType();

                log.debug("return message success, id: {}, messageType: {}, key: {}, responseMessage: {}", id, messageType, key, responseMessage);
            }

        } catch (Exception ex) {
            log.error("return message error, id: {}, key: {}, responseMessage: {}, cause by:", id, key, responseMessage, ex);
        } finally {

            CompletableFuture<Message> completableFuture = pendingResponses.remove(message.getId());
            if (Objects.nonNull(completableFuture)) {
                completableFuture.complete(message);
            }
        }

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
     * 发送消息前
     *
     * @param: messageType
     * @param: cacheMessage
     * @return: com.aiocloud.gateway.cache.client.protocol.Message
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-07 14:09
     * @since 1.0.0
     */
    private Message beforeSendMessage(MessageTypeEnum messageType, CacheMessage cacheMessage) {

        Message message = new Message();
        message.setMessageType(messageType);
        message.setBody(JSONObject.toJSONString(cacheMessage));
        message.setId(SnowflakeIdGeneratorUtil.generateId());

        return message;
    }

    /**
     * 发送消息
     *
     * @param: message
     * @param: completableFuture
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-07 14:08
     * @since 1.0.0
     */
    public void sendMessage(Message message, CompletableFuture<Message> completableFuture) {

        try {

            pendingResponses.put(message.getId(), completableFuture);
            this.ctx.writeAndFlush(message);

        } catch (Exception ex) {
            log.error("send message error, session id: {}, message id: {}, cause by:", message.getSessionId(), message.getId(), ex);
        }
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

        try {

            CacheMessage cacheMessage = new CacheMessage();
            cacheMessage.setKey(key);
            cacheMessage.setValue(value);

            CompletableFuture<Message> completableFuture = new CompletableFuture<>();
            Message message = beforeSendMessage(MessageTypeEnum.REQUEST_SET, cacheMessage);
            sendMessage(message, completableFuture);

            completableFuture.get();

            log.debug("send message complete, key: {}", key);

        } catch (Exception ex) {
            log.error("send message error, key: {}, cause by:", key, ex);
        }
    }

    /**
     * 发送消息并且等待获取消息返回
     *
     * @param: key
     * @return: java.lang.Object
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-07 14:06
     * @since 1.0.0
     */
    public Object sendAndGetMessage(String key) {

        CacheMessage cacheMessage = new CacheMessage();
        cacheMessage.setKey(key);

        try {

            CompletableFuture<Message> completableFuture = new CompletableFuture<>();
            Message message = beforeSendMessage(MessageTypeEnum.REQUEST_GET, cacheMessage);
            sendMessage(message, completableFuture);

            return getCacheValueFromReturnMessage(completableFuture);

        } catch (Exception ex) {
            log.error("send and get message error, key: {}, cause by:", key, ex);
        }

        return null;
    }

    /**
     * 从返回的消息中获取缓存的值
     *
     * @param: future
     * @return: java.lang.Object
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-07 14:06
     * @since 1.0.0
     */
    public Object getCacheValueFromReturnMessage(CompletableFuture<Message> future) throws ExecutionException, InterruptedException {

        Message returnMessage = future.get();

        String body = returnMessage.getBody();
        if (StrUtil.isNotEmpty(body)) {

            CacheMessage cacheMessage = JSONObject.parseObject(body, CacheMessage.class);
            String key = cacheMessage.getKey();
            String responseMessage = cacheMessage.getResponseMessage();

            log.debug("get cache success, key: {}, responseMessage: {}", key, responseMessage);

            return cacheMessage.getValue();
        }

        return null;
    }
}