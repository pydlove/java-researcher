package com.aiocloud.gateway.cache.client.pool;

import com.aiocloud.gateway.cache.client.CacheClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @description: CacheClientPool.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-06 17:48
 */
public class CacheClientPool {

    private final GenericObjectPool<CacheClient> pool;

    public CacheClientPool(String host, int port) {

        GenericObjectPoolConfig<CacheClient> config = new GenericObjectPoolConfig<>();

        // 最大连接数
        config.setMaxTotal(10);

        // 最大空闲连接数
        config.setMaxIdle(5);

        // 最小空闲连接数
        config.setMinIdle(2);

        pool = new GenericObjectPool<>(new CacheClientFactory(host, port), config);
    }

    /**
     * borrowObject
     *
     * @return: com.aiocloud.gateway.cache.client.CacheClient
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-06 18:09
     * @since 1.0.0
     */
    public CacheClient borrowObject() throws Exception {
        return pool.borrowObject();
    }

    /**
     * 返回 缓存客户端对象
     *
     * @param: cacheClient
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-06 18:14
     * @since 1.0.0
     */
    public void returnObject(CacheClient cacheClient) {
        pool.returnObject(cacheClient);
    }

    /**
     * close
     *
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-06 18:14
     * @since 1.0.0
     */
    public void close() {
        pool.close();
    }
}
