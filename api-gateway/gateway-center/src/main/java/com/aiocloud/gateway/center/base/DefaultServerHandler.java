package com.aiocloud.gateway.center.base;

import cn.hutool.core.util.BooleanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @description: ServerHandler.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-24 15:53
 */
@Slf4j
public class DefaultServerHandler implements ServerHandler {

    private final WebClient webClient;
    private final String targetUrl;
    private ServerRequest request;
    private HttpMethod httpMethod;

    public DefaultServerHandler(WebClient.Builder webClientBuilder, String targetUrl) {
        this.webClient = webClientBuilder.baseUrl(targetUrl).build();
        this.targetUrl = targetUrl;
    }

    @Override
    public Mono<ServerResponse> handleForwardRequest(ServerRequest request) {

        this.request = request;
        this.httpMethod = request.method();

        // 构建 WebClient 的请求
        WebClient.RequestBodySpec requestBodySpec = constructRequestBodySpec();

        // 处理请求并且获取响应
        return request.bodyToMono(byte[].class)
                .flatMap(body -> getServerResponseByRequest(body, requestBodySpec))
                .switchIfEmpty(getServerResponseByRequest(null, requestBodySpec));
    }

    /**
     * 构建 WebClient 的请求
     *
     * @param: request
     * @return: org.springframework.web.reactive.function.client.WebClient.RequestBodySpec
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-24 16:04
     * @since 1.0.0
     */
    private WebClient.RequestBodySpec constructRequestBodySpec() {

        return webClient.method(httpMethod)
                .uri(uriBuilder -> handleRequestParams(uriBuilder))
                .headers(headers -> headers.addAll(request.headers().asHttpHeaders()));
    }

    /**
     * 发送请求并返回响应
     *
     * @param: body
     * @param: requestBodySpec
     * @return: reactor.core.publisher.Mono<org.springframework.web.reactive.function.server.ServerResponse>
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-24 16:09
     * @since 1.0.0
     */
    private Mono<ServerResponse> getServerResponseByRequest(byte[] body, WebClient.RequestBodySpec requestBodySpec) {

        if (Objects.isNull(body)) {
            body = new byte[0];
        }

        // 针对 Post 或者 Put 的请求，将请求体插入到 WebClient 的请求中
        if (HttpMethodSelector.checkIsPostOrPut(request.method())) {

            log.info("Received request body from {}: params: {}", targetUrl, new String(body, StandardCharsets.UTF_8));
            requestBodySpec.body(BodyInserters.fromValue(body));
        }

        // 发送请求并返回响应
        return requestBodySpec
                .exchangeToMono(clientResponse -> handleServerResponse(clientResponse))
                .doOnError(e -> log.error("Error forwarding empty body request to {}: {}", targetUrl, e.getMessage()))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal Server Error"));
    }

    /**
     * 处理请求参数
     *
     * @param: uriBuilder
     * @return: java.net.URI
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-24 16:09
     * @since 1.0.0
     */
    private URI handleRequestParams(UriBuilder uriBuilder) {

        // 如果不是 POST 或者 PUT 请求，就不需要处理请求参数了
        if (HttpMethodSelector.checkIsPostOrPut(request.method())) {
            return uriBuilder.build();
        }

        MultiValueMap<String, String> params = request.queryParams();
        log.info("Received request params from {}: params: {}", targetUrl, params);

        return uriBuilder.queryParams(params).build();
    }

    /**
     * 处理请求的响应
     *
     * @param: clientResponse
     * @return: reactor.core.publisher.Mono<org.springframework.web.reactive.function.server.ServerResponse>
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-24 16:11
     * @since 1.0.0
     */
    private Mono<ServerResponse> handleServerResponse(ClientResponse clientResponse) {

        Mono<byte[]> responseBodyMono = clientResponse.bodyToMono(byte[].class);

        return responseBodyMono.flatMap(responseBody -> getServerResponse(clientResponse, responseBody));
    }

    /**
     * 获取响应
     *
     * @param: clientResponse
     * @param: responseBody
     * @return: reactor.core.publisher.Mono<org.springframework.web.reactive.function.server.ServerResponse>
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-24 16:17
     * @since 1.0.0
     */
    private Mono<ServerResponse> getServerResponse(ClientResponse clientResponse, byte[] responseBody) {

        // 将字节数组转换为字符串
        String responseBodyString = new String(responseBody, StandardCharsets.UTF_8);
        HttpStatusCode httpStatusCode = clientResponse.statusCode();
        log.info("Received response from {}: Status Code: {}, Response Body: {}", targetUrl, httpStatusCode, responseBodyString);

        // 构建响应
        return ServerResponse.status(httpStatusCode)
                .headers(headers -> headers.addAll(clientResponse.headers().asHttpHeaders()))
                .body(BodyInserters.fromValue(responseBody))
                .doOnNext(response -> {

                    // 非 200 的响应我需要打印日志
                    if (BooleanUtil.isFalse(httpStatusCode.is2xxSuccessful())) {
                        log.warn("Non-successful response from target: Status Code: {}, Response Body: {}", httpStatusCode, new String(responseBody, StandardCharsets.UTF_8));
                    }
                });
    }
}
