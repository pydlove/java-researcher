package com.aiocloud.gateway.cache.model;

import lombok.Data;

/**
 *
 * @description: CacheMessage.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-01-06 15:00 
 */
@Data
public class CacheMessage {

    private String key;

    private Object value;

    private String responseMessage;
}
