package org.aiocloud.demo.flux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 *
 * @description:  
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-18 10:01
 */
public class MonoMain {

    private static final Logger log = LoggerFactory.getLogger(MonoMain.class);

    public static void main(String[] args) {

        Mono<Integer> mono = Mono.just(1);
        mono.subscribe(c -> {
            log.info("c:{}", c);
        });
    }
}
