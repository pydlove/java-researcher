package org.aiocloud.demo.flux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 *
 * @description:  
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-18 15:35
 */
@Slf4j
public class ErrorMain {

    public static void main(String[] args) {

        Flux.just("a", "b", "c")
                .doOnNext(c -> {

                    if ("b".equals(c)) {
                        throw new NullPointerException("error: " + c);
                    }
                })
                .onErrorReturn(NullPointerException.class, "error return")
                .subscribe(c -> log.info("onErrorReturn: {}", c));


        Flux.just("a", "b", "c")
                .doOnNext(c -> {

                    if ("b".equals(c)) {
                        throw new NullPointerException("error: " + c);
                    }
                })
                .onErrorResume(err -> {
                    return Flux.just("e", "f", "g");
                })
                .subscribe(c -> log.info("onErrorResume: {}", c));


        Flux.just("a", "b", "c")
                .doOnNext(c -> {

                    if ("b".equals(c)) {
                        throw new NullPointerException("error: " + c);
                    }
                })
                .doOnError(error -> {
                    log.error("doOnError: {}", error.getMessage());
                })
                .subscribe(c -> log.info("doOnError: {}", c));
    }
}
