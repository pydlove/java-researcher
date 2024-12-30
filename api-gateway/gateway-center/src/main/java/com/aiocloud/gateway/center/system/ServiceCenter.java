package com.aiocloud.gateway.center.system;

import com.aiocloud.gateway.center.system.loadbalance.LoadBalanceFactory;
import com.aiocloud.gateway.config.ServiceConfig;
import com.aiocloud.gateway.core.config.ServiceRegistryConfig;
import com.aiocloud.gateway.core.registry.ServiceInstance;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: ServiceCenter.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-25 17:24
 */
@RequiredArgsConstructor
@Component
public class ServiceCenter {

    private final ServiceConfig serviceConfig;
    private final LoadBalanceFactory loadBalanceFactory;

    private static final Map<String, List<ServiceInstance>> SERVICE_CONTAINER = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param: serviceInstance
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-25 17:30
     * @since 1.0.0
     */
    public void registerService(ServiceInstance serviceInstance) {

        String serviceName = serviceInstance.getName();
        List<ServiceInstance> serviceInstances = SERVICE_CONTAINER.computeIfAbsent(serviceName, k -> new ArrayList<>());
        serviceInstances.add(serviceInstance);
    }

    /**
     * 根据服务名称获取服务
     *
     * @param: serviceName
     * @return: com.aiocloud.gateway.core.registry.ServiceInstance
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-25 17:31
     * @since 1.0.0
     */
    public ServiceInstance getServiceInfo(String serviceName) {
        List<ServiceInstance> serviceInstances = SERVICE_CONTAINER.get(serviceName);

        // 通过负载均衡的算法获取服务信息
        return loadBalanceFactory.getServerLoadBalance().selectServer(serviceInstances);
    }
}
