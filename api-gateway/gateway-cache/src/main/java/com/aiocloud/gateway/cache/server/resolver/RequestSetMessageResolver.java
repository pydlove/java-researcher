package com.aiocloud.gateway.cache.server.resolver;

import cn.hutool.core.util.StrUtil;
import com.aiocloud.gateway.cache.base.common.CacheCallback;
import com.aiocloud.gateway.cache.base.constants.SystemConstant;
import com.aiocloud.gateway.cache.client.CacheClient;
import com.aiocloud.gateway.cache.server.protocol.Message;
import com.aiocloud.gateway.cache.server.protocol.MessageTypeEnum;
import com.aiocloud.gateway.cache.core.CacheManager;
import com.aiocloud.gateway.cache.model.CacheMessage;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: RequestMessageResolver.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-06 14:54
 */
public class RequestSetMessageResolver extends CommonMessageResolver implements MessageResolver {

    private static final Logger log = LoggerFactory.getLogger(CacheClient.class);

    @Override
    public boolean support(Message message) {
        return message.getMessageType() == MessageTypeEnum.REQUEST_SET;
    }

    @Override
    public Message resolve(Message message) {

        Message response = aroundResolve(message, new CacheCallback<CacheMessage>() {
            @Override
            public void execute(CacheMessage cacheMessage) {

                String key = cacheMessage.getKey();
                Object value = cacheMessage.getValue();

                // 设置缓存
                CacheManager.put(key, value);

                // 为了返回传输时候浪费传输性能，这里设置为 null
                cacheMessage.setValue(null);
            }
        });

        response.setMessageType(MessageTypeEnum.REQUEST_SET);

        return response;
    }
}
