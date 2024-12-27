package com.aiocloud.gateway.core.registry;

import com.aiocloud.gateway.core.config.ServiceRegistryConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * @description: ServiceRegistryClient.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-25 14:34
 */
@Slf4j
@Component
public class ServiceRegistryClient {

    @Resource
    private ServiceRegistryConfig config;

    @Resource
    private WebClient.Builder webClientBuilder;

    /**
     * 开始注册服务
     *
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-25 14:56
     * @since 1.0.0
     */
    @EventListener(ApplicationReadyEvent.class)
    public void registerService() {

        String serviceName = config.getServiceName();
        String registryUrl = config.getRegistryUrl() + "/gateway-service/register/do";
        try {

            // 获取注册中心的注册接口地址
            ServiceInstance serviceInstance = new ServiceInstance(serviceName, config.getServiceUrl());
            log.info("register service, service name: {}, url: {}", serviceName, registryUrl);

            // 通过响应式的方式将请求发送到注册中心
            WebClient webClient = webClientBuilder.baseUrl(registryUrl).build();

            doRegisterService(registryUrl, serviceInstance)
                    .map(response -> ServerSentEvent.builder(response).build())
                    .subscribe();

        } catch (Exception ex) {
            log.error("register service error, service name: {}, url: {}, caused by:", serviceName, registryUrl, ex);
            throw new RuntimeException("register service error, service name: " + serviceName + ", url: " + registryUrl, ex);
        }
    }


    public Mono<String> doRegisterService(String registryUrl, Object serviceInstance) {

        WebClient webClient = webClientBuilder.baseUrl(registryUrl).build();

        return webClient.post()
                .uri(registryUrl)
                .bodyValue(serviceInstance)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("register service success, response: {}", response))
                .doOnError(WebClientResponseException.class, ex ->
                        log.error("Failed to register service, status code: {}, caused by:", ex.getStatusCode(), ex));
    }

}

