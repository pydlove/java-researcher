package org.aiocloud.demo.flux;


import cn.hutool.core.thread.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * @description:
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-18 9:47
 */
public class FluxMain {

    private static final Logger log = LoggerFactory.getLogger(FluxMain.class);

    public static void main(String[] args) {

        // Flux N个元素的流
        Flux<Integer> just = Flux.just(1, 2, 3, 4, 5);
        just.subscribe(c -> {
            log.info("c1:{}", c);
        });

        just.subscribe(c -> {
            log.info("c2:{}", c);
        });

        // 每秒产生一个
        Flux<Long> flux = Flux.interval(Duration.ofSeconds(1));
        flux.subscribe(c -> {
            log.info("c3:{}", c);
        });


        Flux<Object> emptyFlux = Flux.empty()
                .delayElements(Duration.ofSeconds(1))
                // 流数据到达时触发的事件
                .doOnNext(c -> log.info("doOnNext c: {}", c))
                // 流数据和信号到达时触发的事件
                .doOnEach(s -> log.info("doOnEach: {}", s))
                .doOnComplete(() -> log.info("emptyFlux 流结束了..."));

        emptyFlux.subscribe(c -> {
            log.info("c4:{}", c);
        });

        Flux<Integer> rangeFlux = Flux.range(1, 7)
                .delayElements(Duration.ofSeconds(1))
                .doOnComplete(() -> log.info("rangeFlux 流结束了..."));

        rangeFlux.subscribe(c -> {
            log.info("c5:{}", c);
        });

        ThreadUtil.sleep(20000);

        // concat
        Flux<Integer> concat = Flux.concat(Flux.just(1, 2, 3, 4), Flux.range(5, 10));
        concat.subscribe(c -> log.info("concat:{}", c));
    }
}
