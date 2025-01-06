package com.aiocloud.gateway.cache.client.resolver;

import cn.hutool.core.util.StrUtil;
import com.aiocloud.gateway.cache.base.constants.SystemConstant;
import com.aiocloud.gateway.cache.client.CacheClient;
import com.aiocloud.gateway.cache.client.protocol.Message;
import com.aiocloud.gateway.cache.client.protocol.MessageTypeEnum;
import com.aiocloud.gateway.cache.model.CacheMessage;
import com.alibaba.fastjson.JSONObject;
import org.checkerframework.checker.units.qual.C;
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
public class RequestMessageResolver implements MessageResolver {


    private static final Logger log = LoggerFactory.getLogger(CacheClient.class);

    private static final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public boolean support(Message message) {
        return message.getMessageType() == MessageTypeEnum.REQUEST;
    }

    @Override
    public Message resolve(Message message) {

        int index = counter.getAndIncrement();
        Message response = new Message();
        response.setMessageType(MessageTypeEnum.RESPONSE);

        String body = message.getBody();
        if (StrUtil.isEmpty(body)) {
            return response;
        }

        CacheMessage cacheMessage = JSONObject.parseObject(body, CacheMessage.class);
        String key = cacheMessage.getKey();

        try {

            cacheMessage.setResponseMessage(SystemConstant.SUCCESS);

            // 设置缓存

        } catch (Exception ex) {

            cacheMessage.setResponseMessage(SystemConstant.FAIL);
            log.error("set cache error, key: {}, cause by:", key, ex);
        }

        response.setBody(JSONObject.toJSONString(cacheMessage));

        return response;
    }
}
