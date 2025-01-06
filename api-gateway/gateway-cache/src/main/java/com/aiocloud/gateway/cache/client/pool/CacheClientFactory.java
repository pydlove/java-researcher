package com.aiocloud.gateway.cache.client.pool;

import com.aiocloud.gateway.cache.client.CacheClient;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.checkerframework.checker.units.qual.C;

/**
 *
 * @description: CacheClientFactory.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-01-06 17:49 
 */
public class CacheClientFactory extends BasePooledObjectFactory<CacheClient> {

    private final String host;
    private final int port;

    public CacheClientFactory(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public CacheClient create() throws Exception {
        return new CacheClient(host, port);
    }

    @Override
    public PooledObject<CacheClient> wrap(CacheClient cacheClient) {
        return new DefaultPooledObject<>(cacheClient);
    }

    @Override
    public void destroyObject(PooledObject<CacheClient> p, DestroyMode destroyMode) throws Exception {
        p.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<CacheClient> p) {
        return p.getObject().isActive();
    }
}
