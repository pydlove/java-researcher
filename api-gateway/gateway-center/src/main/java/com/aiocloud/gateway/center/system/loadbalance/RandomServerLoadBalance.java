package com.aiocloud.gateway.center.system.loadbalance;

import com.aiocloud.gateway.core.registry.ServiceInstance;

import java.util.List;

/**
 *
 * @description: RandomServerLoadBalance.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-27 13:34 
 */
public class RandomServerLoadBalance implements ServerLoadBalance {

    @Override
    public ServiceInstance selectServer(List<ServiceInstance> serviceInstances) {
        return null;
    }

}
