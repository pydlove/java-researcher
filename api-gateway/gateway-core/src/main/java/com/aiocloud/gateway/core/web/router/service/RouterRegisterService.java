package com.aiocloud.gateway.core.web.router.service;

import com.aiocloud.gateway.core.web.router.dto.RouterRegisterDTO;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Mono;

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

    Mono<ServerSentEvent<String>> registerService(RouterRegisterDTO routerRegister);
}
