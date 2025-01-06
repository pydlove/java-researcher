package com.aiocloud.gateway.cache.client.pool;

import com.aiocloud.gateway.cache.conf.SystemProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: CacheClientManager.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-06 18:10
 */
@Slf4j
public class CacheClientManager {

    private CacheClientPool cacheClientPool;

    private static class CacheClientManagerHolder {
        private static final CacheClientManager INSTANCE = new CacheClientManager();
    }

    private CacheClientManager() {
        this.cacheClientPool = new CacheClientPool(SystemProperties.serverHost, SystemProperties.serverPort);
    }

    public static CacheClientManager getInstance() {
        return CacheClientManagerHolder.INSTANCE;
    }

    /**
     * 设置缓存
     *
     * @param: key
     * @param: value
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-06 18:14
     * @since 1.0.0
     */
    public void setCache(String key, Object value) {

        try {

            cacheClientPool.borrowObject().setCache(key, value);

        } catch (Exception ex) {
            log.error("set cache error, key: {}, cause by:", key, ex);
        }
    }
}
