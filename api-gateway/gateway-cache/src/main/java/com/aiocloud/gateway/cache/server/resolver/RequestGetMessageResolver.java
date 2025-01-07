package com.aiocloud.gateway.cache.server.resolver;

import cn.hutool.core.util.StrUtil;
import com.aiocloud.gateway.cache.base.common.CacheCallback;
import com.aiocloud.gateway.cache.base.constants.SystemConstant;
import com.aiocloud.gateway.cache.server.protocol.Message;
import com.aiocloud.gateway.cache.server.protocol.MessageTypeEnum;
import com.aiocloud.gateway.cache.core.CacheManager;
import com.aiocloud.gateway.cache.model.CacheMessage;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class RequestGetMessageResolver extends CommonMessageResolver implements MessageResolver {

    private static final Logger log = LoggerFactory.getLogger(MessageResolver.class);

    @Override
    public boolean support(Message message) {
        return message.getMessageType() == MessageTypeEnum.REQUEST_GET;
    }

    @Override
    public Message resolve(Message message) {

        Message response = aroundResolve(message, new CacheCallback<CacheMessage>() {
            @Override
            public void execute(CacheMessage cacheMessage) {

                String key = cacheMessage.getKey();

                // 获取缓存
                Object value = CacheManager.get(key);
                cacheMessage.setValue(value);
            }
        });

        response.setMessageType(MessageTypeEnum.REQUEST_GET);

        return response;
    }
}
