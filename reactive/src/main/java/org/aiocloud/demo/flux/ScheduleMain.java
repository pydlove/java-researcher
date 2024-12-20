package org.aiocloud.demo.flux;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @description:  
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-18 15:23
 */
public class ScheduleMain {

    public static void main(String[] args) {

        // Schedulers.single() 单线程
        // Schedulers.immediate() 默认无执行上下文，当前线程运行所有操作
        Flux.range(1, 10)
                .publishOn(Schedulers.single())
                .log()
                .subscribeOn(Schedulers.immediate())
                .subscribe();

        // Schedulers.boundedElastic() 有界，弹性深度，10C, 队列长度 100K

        // 自定义线程池
        // Schedulers.fromExecutor(new ThreadPoolExecutor(10, 10, 0L, java.util.concurrent.TimeUnit.MILLISECONDS, new java.util.concurrent.LinkedBlockingQueue<Runnable>(100000)))
    }
}
