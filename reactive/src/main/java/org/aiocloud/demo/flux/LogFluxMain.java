package org.aiocloud.demo.flux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * @description:
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-18 11:13
 */
@Slf4j
public class LogFluxMain {

    public static void main(String[] args) {

        Flux<Integer> range = Flux.range(1, 10)
                .log();
        range.subscribe(c -> log.info("range:{}", c));
    }
}
