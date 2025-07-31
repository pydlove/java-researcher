package com.aiocloud.gateway.center.router.controller;


import com.aiocloud.gateway.center.router.service.RouterRegisterService;
import com.aiocloud.gateway.base.common.CommonResponse;
import com.aiocloud.gateway.core.registry.ServiceInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: RegistrationController.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-20 14:02
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/register")
public class RouterRegisterController {

    private final RouterRegisterService routerRegisterService;

    /**
     * 注册服务
     *
     * @param: serviceInstance
     * @return: reactor.core.publisher.Mono<org.springframework.http.codec.ServerSentEvent < java.lang.String>>
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-25 14:59
     * @since 1.0.0
     */
    @PostMapping("/do")
    public CommonResponse<String> registerService(@RequestBody ServiceInstance serviceInstance) {
        return routerRegisterService.registerService(serviceInstance);
    }
}
