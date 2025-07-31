package com.aiocloud.gateway.base.cache;

import com.aiocloud.gateway.cache.client.pool.CacheClientManager;
import com.aiocloud.gateway.core.registry.ServiceInstance;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.K;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * @description: CacheTemplate.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-08 10:51
 */
@Component
public class CacheTemplate {

    private ObjectMapper objectMapper;

    public CacheTemplate() {
        this.objectMapper = objectMapper;
    }

    public <V> void put(String key, List<V> value) {

        CacheClientManager.getInstance().setCache(key, value);
    }

    public <V> void put(String key, V value) {

        CacheClientManager.getInstance().setCache(key, value);
    }

    public Object get(String key) {

        return CacheClientManager.getInstance().getCache(key);
    }
}
