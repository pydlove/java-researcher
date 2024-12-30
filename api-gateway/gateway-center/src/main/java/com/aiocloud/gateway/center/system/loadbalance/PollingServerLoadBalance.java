package com.aiocloud.gateway.center.system.loadbalance;

import cn.hutool.core.collection.CollUtil;
import com.aiocloud.gateway.core.registry.ServiceInstance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: PollingServerLoadBalance.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-27 10:45
 */
public class PollingServerLoadBalance implements ServerLoadBalance {

    private static AtomicInteger currentIndex = new AtomicInteger(0);

    /**
     * 轮询算法
     *
     * @param: serviceInstances
     * @return: com.aiocloud.gateway.core.registry.ServiceInstance
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-27 13:38
     * @since 1.0.0
     */
    @Override
    public ServiceInstance selectServer(List<ServiceInstance> serviceInstances) {

        if (CollUtil.isEmpty(serviceInstances)) {
            return null;
        }

        // 更新索引，循环到列表末尾时重置为0
        // 这块要考虑到线程安全的问题
        int index = currentIndex.getAndIncrement() % serviceInstances.size();


        // 获取当前索引的服务实例
        ServiceInstance instance = serviceInstances.get(index);

        return instance;
    }

}
