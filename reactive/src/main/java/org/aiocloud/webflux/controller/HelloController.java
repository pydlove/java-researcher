package org.aiocloud.webflux.controller;

import org.aiocloud.webflux.exception.AppException;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/mono")
    public Mono<String> helloMono(){
        return Mono.just("hello Mono");
    }

    @GetMapping("/flux")
    public Flux<String> helloFlux(){
        return Flux.just("hello", "flux");
    }

    @GetMapping(value = "/flux/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> helloFluxSSE(){
        return Flux.range(1, 10)
                .map(i -> "hello flux, no " + i)
                .delayElements(Duration.ofSeconds(1));
    }

    @GetMapping(value = "/flux/sse1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> helloFluxSSE1(){
        return Flux.range(1, 10)
                .map(i -> {
                   return ServerSentEvent.builder()
                           .id(i + "")
                            .comment("hello server sent event " + i)
                            .data("hello")
                            .event("hello")
                            .build();
                })
                .delayElements(Duration.ofSeconds(1));
    }

    @GetMapping(value = "/exception")
    public Flux<String> exception(){

        if (true) {
            throw new AppException("错误了");
        }

        return Flux.just("hello");
    }
}
