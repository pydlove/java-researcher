package com.aiocloud.gateway.center.system;

import com.aiocloud.gateway.base.cache.CacheTemplate;
import com.aiocloud.gateway.cache.client.pool.CacheClientManager;
import com.aiocloud.gateway.center.system.loadbalance.LoadBalanceFactory;
import com.aiocloud.gateway.config.ServiceConfig;
import com.aiocloud.gateway.core.config.ServiceRegistryConfig;
import com.aiocloud.gateway.core.registry.ServiceInstance;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final CacheTemplate cacheTemplate;

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
        // List<ServiceInstance> serviceInstances = SERVICE_CONTAINER.computeIfAbsent(serviceName, k -> new ArrayList<>());
        // serviceInstances.add(serviceInstance);

        Object result = cacheTemplate.get(serviceName);
        List<ServiceInstance> serviceInstances;
        if (Objects.isNull(result)) {
            serviceInstances = new ArrayList<>();
        } else {

            // 这里要校验是否重复注册同一个地址，重复的去处
            serviceInstances = ((List<?>) result).stream()
                    .filter(item -> item instanceof ServiceInstance)
                    .map(ServiceInstance.class::cast)
                    .filter(item -> !Objects.equals(item.getAddress(), serviceInstance.getAddress()))
                    .collect(Collectors.toList());
        }

        serviceInstances.add(serviceInstance);
        cacheTemplate.put(serviceName, serviceInstances);
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

        // List<ServiceInstance> serviceInstances = SERVICE_CONTAINER.get(serviceName);

        Object result = cacheTemplate.get(serviceName);
        if (Objects.isNull(result)) {
            return null;
        }

        List<ServiceInstance> serviceInstances = (List<ServiceInstance>) result;

        // 通过负载均衡的算法获取服务信息
        return loadBalanceFactory.getServerLoadBalance().selectServer(serviceInstances);
    }
}
