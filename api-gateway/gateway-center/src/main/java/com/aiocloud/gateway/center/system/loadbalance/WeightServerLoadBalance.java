package com.aiocloud.gateway.center.system.loadbalance;

import com.aiocloud.gateway.core.registry.ServiceInstance;

import java.util.List;

/**
 *
 * @description: WeightServerLoadBalance.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-27 14:03
 */
public class WeightServerLoadBalance implements ServerLoadBalance {

    @Override
    public ServiceInstance selectServer(List<ServiceInstance> serviceInstances) {
        return null;
    }

}
