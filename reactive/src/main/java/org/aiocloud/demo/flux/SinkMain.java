package org.aiocloud.demo.flux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-18 13:56
 */
@Slf4j
public class SinkMain {

    public static void main(String[] args) {

        Flux<Object> generate = Flux.generate(sink -> {
            for (int i = 0; i < 100; i++) {
                sink.next("test-sink-" + i);
            }
        });

        generate.subscribe(c -> log.info("sink: {}", c));

        Flux<Object> flux = Flux.generate(AtomicInteger::new, (state, sink) -> {

            int num = state.getAndIncrement();
            if (num <= 10) {
                sink.next("test-flux-" + state);
            } else {
                sink.complete();
            }

            if (num == 7) {
                sink.error(new RuntimeException("custom exception"));
            }

            return state;
        });

        flux.subscribe(c -> log.info("sink1: {}", c));
    }
}
