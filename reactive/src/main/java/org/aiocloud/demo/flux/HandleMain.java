package org.aiocloud.demo.flux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * @description:
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-18 15:17
 */
@Slf4j
public class HandleMain {

    public static void main(String[] args) {

        Flux.range(1, 10)
                .handle((value, sink) -> {
                    if (value % 2 == 0) {
                        sink.next("test: " + value);
                    }
                })
                .subscribe(c -> log.info("handle: {}", c));
    }
}
