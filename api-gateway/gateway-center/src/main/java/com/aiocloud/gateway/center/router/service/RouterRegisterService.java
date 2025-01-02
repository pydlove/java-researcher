package com.aiocloud.gateway.center.router.service;

import com.aiocloud.gateway.base.common.CommonResponse;
import com.aiocloud.gateway.core.registry.ServiceInstance;

/**
 *
 * @description: RouterRegisterService.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-20 14:05 
 */
public interface RouterRegisterService {

    CommonResponse<String> registerService(ServiceInstance serviceInstance);

    CommonResponse<String> selfRegisterService();
}
