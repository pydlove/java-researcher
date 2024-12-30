package com.aiocloud.gateway.center.system.loadbalance;

import com.aiocloud.gateway.core.registry.ServiceInstance;

import java.util.List;

/**
 * @description: ConsistentHashServerLoadBalance.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-27 13:35
 */
public class ConsistentHashServerLoadBalance implements ServerLoadBalance {

    @Override
    public ServiceInstance selectServer(List<ServiceInstance> serviceInstances) {
        return null;
    }
}
