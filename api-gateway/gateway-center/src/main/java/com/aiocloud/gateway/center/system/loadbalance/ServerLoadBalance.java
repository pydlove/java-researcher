package com.aiocloud.gateway.center.system.loadbalance;

import com.aiocloud.gateway.core.registry.ServiceInstance;

import java.util.List;

/**
 *
 * @description: ServerLoadBalance.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-27 10:44 
 */
public interface ServerLoadBalance {

    ServiceInstance selectServer(List<ServiceInstance> serviceInstances);
}
