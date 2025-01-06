package com.aiocloud.gateway.cache.client.protocol;

import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @description: Message.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-01-06 13:57 
 */
@Data
public class Message {

    private int magicNumber;
    private byte mainVersion;
    private byte subVersion;
    private byte modifyVersion;
    private String sessionId;

    private MessageTypeEnum messageType;
    private Map<String, String> attachments = new HashMap<>();
    private String body;

    public Map<String, String> getAttachments() {
        return Collections.unmodifiableMap(attachments);
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments.clear();
        if (null != attachments) {
            this.attachments.putAll(attachments);
        }
    }

    public void addAttachment(String key, String value) {
        attachments.put(key, value);
    }
}
