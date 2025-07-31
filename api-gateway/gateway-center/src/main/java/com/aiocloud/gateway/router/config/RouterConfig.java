package com.aiocloud.gateway.router.config;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.aiocloud.gateway.base.ApplicationContextProvider;
import com.aiocloud.gateway.center.router.service.RouterRegisterService;
import com.aiocloud.gateway.router.server.DefaultServerHandler;
import com.aiocloud.gateway.router.server.HttpUrlSelector;
import com.aiocloud.gateway.router.server.SelfServerHandler;
import com.aiocloud.gateway.router.server.ServerHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import reactor.core.publisher.Mono;

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

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final ApplicationContextProvider applicationContextProvider;
    private final HttpUrlSelector httpUrlSelector;
    private final AuthenticationCheck authenticationCheck;

    @Value("${service.registry.service-name:gateway-service}")
    private String gatewayServiceName;

    @Value("${service.registry.registry-url:http://localhost:8080}")
    private String gatewayRegistryUrl;

    @Bean
    public RouterFunction<ServerResponse> customRouterFunction() {
        return route()
                .filter((request, next) -> doFilter(request, next))
                .route(RequestPredicates.path("/**"), this::forwardRequest)
                .build();

    }

    /**
     * 处理过滤
     *
     * @param: request
     * @param: next
     * @return: reactor.core.publisher.Mono<org.springframework.web.reactive.function.server.ServerResponse>
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-26 18:18
     * @since 1.0.0
     */
    private Mono<ServerResponse> doFilter(ServerRequest request, HandlerFunction<ServerResponse> next) {

        log.info("Received request for path: {}", request.path());

        String path = request.path();
        if (StrUtil.isEmpty(path)) {
            throw new RuntimeException();
        }

        // 进行鉴权
        if (BooleanUtil.isFalse(authenticationCheck.isAccess(request))) {

            log.debug("Received request for path: {} unauthorized, return response code: {}", request.path(), HttpStatus.UNAUTHORIZED);
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 网关自己的请求无需转发，直接映射自己的方法
        if (BooleanUtil.isTrue(path.startsWith("/" + gatewayServiceName))) {
            return forwardSelfRequest(request);
        }

        return next.handle(request);
    }

    /**
     * 处理注册中心自己的请求
     *
     * @param: request
     * @return: reactor.core.publisher.Mono<org.springframework.web.reactive.function.server.ServerResponse>
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-26 18:18
     * @since 1.0.0
     */
    public Mono<ServerResponse> forwardSelfRequest(ServerRequest request) {

        ServerHandler selfServerHandler = new SelfServerHandler(requestMappingHandlerMapping, applicationContextProvider, gatewayServiceName);
        return selfServerHandler.handleForwardRequest(request);
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
        String targetUrl = httpUrlSelector.getTargetUrl(request);

        // 创建 serverHandler
        ServerHandler serverHandler = new DefaultServerHandler(webClientBuilder, targetUrl);

        // 处理请求转发
        return serverHandler.handleForwardRequest(request);
    }

}