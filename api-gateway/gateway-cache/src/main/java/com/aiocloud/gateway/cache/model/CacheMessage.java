package com.aiocloud.gateway.cache.model;

import lombok.Data;

import java.io.Serializable;

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
public class CacheMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;

    private Object value;

    private String responseMessage;
}
