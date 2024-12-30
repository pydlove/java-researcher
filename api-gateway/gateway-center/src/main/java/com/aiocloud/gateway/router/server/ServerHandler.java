package com.aiocloud.gateway.router.server;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 *
 * @description: ServerHandler.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-26 18:05 
 */
public interface ServerHandler {

    Mono<ServerResponse> handleForwardRequest(ServerRequest request);
}
