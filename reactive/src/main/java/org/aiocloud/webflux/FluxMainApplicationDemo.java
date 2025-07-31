package org.aiocloud.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

import java.io.IOException;

/**
 * @description:
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-18 20:52
 */
@Slf4j
@SpringBootApplication
public class FluxMainApplicationDemo {

    public static void main(String[] args) throws IOException {

        HttpHandler handler = (ServerHttpRequest request, ServerHttpResponse response) -> {

            log.info("request url: {}", request.getURI());

            response.getHeaders().set("Content-Type", "text/plain");

            return Mono.empty();
        };

        ReactorHttpHandlerAdapter reactorHttpHandlerAdapter = new ReactorHttpHandlerAdapter(handler);

        HttpServer.create()
                .host("localhost")
                .port(8080)
                .handle(reactorHttpHandlerAdapter)
                .bindNow();

        // 用于阻塞主进程关闭
        System.in.read();
    }

}
