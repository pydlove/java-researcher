package com.aiocloud.gateway.center.system;

import com.aiocloud.gateway.core.registry.ServiceInstance;

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
public class ServiceCenter {

    private static class SingletonHolder {
        private static final ServiceCenter INSTANCE = new ServiceCenter();
    }

    private ServiceCenter() {
    }

    public static ServiceCenter getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final Map<String, ServiceInstance> SERVICE_CONTAINER = new ConcurrentHashMap<>();

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

        SERVICE_CONTAINER.put(serviceInstance.getName(), serviceInstance);
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
        return SERVICE_CONTAINER.get(serviceName);
    }
}
