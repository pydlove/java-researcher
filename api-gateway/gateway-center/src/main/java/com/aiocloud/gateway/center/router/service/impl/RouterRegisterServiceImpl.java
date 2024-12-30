package com.aiocloud.gateway.center.router.service.impl;

import com.aiocloud.gateway.center.constant.SystemConstant;
import com.aiocloud.gateway.center.router.service.RouterRegisterService;
import com.aiocloud.gateway.center.system.ServiceCenter;
import com.aiocloud.gateway.center.system.common.CommonResponse;
import com.aiocloud.gateway.core.registry.ServiceInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: RouterRegisterServiceImpl.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-20 14:05
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class RouterRegisterServiceImpl implements RouterRegisterService {

    private final ServiceCenter serviceCenter;

    @Value("${service.registry.service-name:gateway-service}")
    private String gatewayServiceName;

    @Value("${service.registry.registry-url:http://localhost:8080}")
    private String gatewayRegistryUrl;

    @Override
    public CommonResponse<String> registerService(ServiceInstance serviceInstance) {

        try {

            // 这里暂时使用缓存进行存储注册服务信息，后续可以改为数据库存储
            serviceCenter.registerService(serviceInstance);

            return new CommonResponse(SystemConstant.RESPONSE_SUCCESS);

        } catch (Exception ex) {
            log.error("register service error, caused by:", ex);
        }

        return new CommonResponse(SystemConstant.RESPONSE_FAIL);
    }

    @Override
    public CommonResponse<String> selfRegisterService() {

        try {

            ServiceInstance serviceInstance = new ServiceInstance(gatewayServiceName, gatewayRegistryUrl);
            serviceCenter.registerService(serviceInstance);

            return new CommonResponse(SystemConstant.RESPONSE_SUCCESS);

        } catch (Exception ex) {
            log.error("self register service error, caused by:", ex);
        }

        return new CommonResponse(SystemConstant.RESPONSE_FAIL);
    }
}
