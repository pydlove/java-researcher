package com.aiocloud.gateway.cache.server.resolver;

import cn.hutool.core.util.StrUtil;
import com.aiocloud.gateway.cache.base.common.CacheCallback;
import com.aiocloud.gateway.cache.base.constants.SystemConstant;
import com.aiocloud.gateway.cache.core.CacheManager;
import com.aiocloud.gateway.cache.model.CacheMessage;
import com.aiocloud.gateway.cache.server.protocol.Message;
import com.aiocloud.gateway.cache.server.protocol.MessageTypeEnum;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: CommonMessageResolver.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-07 14:56
 */
@Slf4j
public class CommonMessageResolver {

    /**
     * 环绕处理
     *
     * @param: message
     * @param: cacheCallback
     * @return: com.aiocloud.gateway.cache.server.protocol.Message
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-07 15:13
     * @since 1.0.0
     */
    public Message aroundResolve(Message message, CacheCallback<CacheMessage> cacheCallback) {

        Message response = new Message();
        response.setId(message.getId());
        response.setSessionId(message.getSessionId());

        String body = message.getBody();
        if (StrUtil.isEmpty(body)) {
            return response;
        }

        CacheMessage cacheMessage = JSONObject.parseObject(body, CacheMessage.class);
        String key = cacheMessage.getKey();

        try {

            cacheMessage.setResponseMessage(SystemConstant.SUCCESS);
            cacheCallback.execute(cacheMessage);

        } catch (Exception ex) {

            cacheMessage.setResponseMessage(SystemConstant.FAIL);
            log.error("around resolve cache error, key: {}, cause by:", key, ex);
        }

        response.setBody(JSONObject.toJSONString(cacheMessage));

        return response;
    }
}
