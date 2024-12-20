package org.aiocloud.demo.flux;

import cn.hutool.core.thread.ThreadUtil;
import reactor.core.publisher.Flux;

/**
 *
 * @description:  
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-18 11:41
 */
public class CustomFluxSubscriberMain {

    public static void main(String[] args) {

        Flux<String> flux = Flux.just("a", "b", "c");

        CustomSubscriber customSubscriber = new CustomSubscriber();
        flux.subscribe(customSubscriber);

        ThreadUtil.sleep(20000);
    }
}
