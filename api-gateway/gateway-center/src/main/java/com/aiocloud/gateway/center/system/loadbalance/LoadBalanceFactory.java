package com.aiocloud.gateway.center.system.loadbalance;

import com.aiocloud.gateway.config.ServiceConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @description: LoadBalanceFactory.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-27 10:48
 */
@RequiredArgsConstructor
@Component
public class LoadBalanceFactory {

    private final ServiceConfig serviceConfig;

    /**
     * 根据用户的选择使用对应的负载均衡策略
     *
     * @return: com.aiocloud.gateway.center.system.loadbalance.ServerLoadBalance
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-27 13:35
     * @since 1.0.0
     */
    public ServerLoadBalance getServerLoadBalance() {

        // 这需要实现根据配置文件来选择负载均衡策略
        String strategy = serviceConfig.getStrategy();
        ServerLoadBalanceEnum serverLoadBalanceEnum = ServerLoadBalanceEnum.getEnumByLoadBalance(strategy);
        switch (serverLoadBalanceEnum) {
            case RANDOM:
                return new PollingServerLoadBalance();
            case WEIGHT:
                return new PollingServerLoadBalance();
            case CONSISTENT_HASH:
                return new PollingServerLoadBalance();
            case POLLING:
            default:
                return new PollingServerLoadBalance();
        }
    }
}
