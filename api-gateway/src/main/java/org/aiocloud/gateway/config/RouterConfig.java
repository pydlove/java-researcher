package org.aiocloud.gateway.config;

import cn.hutool.core.util.BooleanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aiocloud.gateway.base.HttpMethodSelector;
import org.aiocloud.gateway.base.HttpUrlSelector;
import org.aiocloud.gateway.base.ServerHandler;
import org.aiocloud.gateway.web.router.service.RouterRegisterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @description: RouterConfig.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-23 14:48
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class RouterConfig {

    private final RouterRegisterService routerRegisterService;
    private final WebClient.Builder webClientBuilder;

    @Bean
    public RouterFunction<ServerResponse> customRouterFunction() {
        return route()
                .route(RequestPredicates.path("/**"), this::forwardRequest)
                .filter((request, next) -> {
                    log.info("Received request for path: {}", request.path());
                    return next.handle(request);
                })
                .build();
    }

    /**
     * forwardRequest
     *
     * @param: request
     * @return: reactor.core.publisher.Mono<org.springframework.web.reactive.function.server.ServerResponse>
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-24 16:29
     * @since 1.0.0
     */
    private Mono<ServerResponse> forwardRequest(ServerRequest request) {

        HttpMethod httpMethod = request.method();

        // 获取 targetUrl
        String targetUrl = HttpUrlSelector.getInstance().getTargetUrl(request);

        // 创建 serverHandler
        ServerHandler serverHandler = new ServerHandler(webClientBuilder, targetUrl);

        // 处理请求转发
        return serverHandler.handleForwardRequest(request);
    }
}