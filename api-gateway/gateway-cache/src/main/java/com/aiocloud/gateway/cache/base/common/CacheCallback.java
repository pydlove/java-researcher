package com.aiocloud.gateway.cache.base.common;

/**
 *
 * @description: CacheCallback.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-01-07 15:00 
 */
public interface CacheCallback<T> {

    void execute(T t);
}
