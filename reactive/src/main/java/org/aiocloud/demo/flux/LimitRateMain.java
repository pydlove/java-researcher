package org.aiocloud.demo.flux;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

/**
 *
 * @description:  
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-18 13:47
 */
@Slf4j
public class LimitRateMain {

    public static void main(String[] args) {

        // 限流
        Flux<Integer> integerFlux = Flux.range(1, 1000)
                .log()
                .limitRate(100);

        integerFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                subscription.request(1);
            }

            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                log.info("data: {}", value);
                ThreadUtil.sleep(100);
            }
        });
    }
}
