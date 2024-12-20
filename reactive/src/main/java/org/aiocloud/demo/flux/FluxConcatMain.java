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
 * @createTime: 2024-12-18 10:53
 */
@Slf4j
public class FluxConcatMain {
    
    public static void main(String[] args) {

        // concat
        Flux<Integer> concat = Flux.concat(Flux.just(1, 2, 3, 4), Flux.range(5, 10));
        concat.subscribe(c -> log.info("concat:{}", c));
    }
}
