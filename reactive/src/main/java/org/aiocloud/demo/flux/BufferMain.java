package org.aiocloud.demo.flux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @description:
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-18 13:41
 */
@Slf4j
public class BufferMain {

    public static void main(String[] args) {

        // 批处理
        Flux<List<Integer>> buffer = Flux.range(1, 10)
                .buffer(3);

        buffer.subscribe(c -> log.info("buffer:{}", c));
    }
}
