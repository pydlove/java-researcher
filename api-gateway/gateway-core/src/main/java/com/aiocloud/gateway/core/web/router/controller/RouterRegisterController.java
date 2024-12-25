package com.aiocloud.gateway.core.web.router.controller;


import com.aiocloud.gateway.core.web.router.dto.RouterRegisterDTO;
import com.aiocloud.gateway.core.web.router.service.RouterRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.imageio.spi.ServiceRegistry;

/**
 *
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

    @PostMapping("/do")
    public Mono<ServerSentEvent<String>> registerService(@RequestBody RouterRegisterDTO routerRegister) {
        return routerRegisterService.registerService(routerRegister);
    }
}
