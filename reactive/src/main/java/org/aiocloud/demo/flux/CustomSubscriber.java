package org.aiocloud.demo.flux;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.SignalType;

/**
 *
 * @description:  
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-18 11:43
 */
@Slf4j
public class CustomSubscriber extends BaseSubscriber<String> {


    private Subscription subscription;

    @Override
    protected void hookOnSubscribe(Subscription subscription) {

        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    protected void hookOnNext(String value) {

        subscription.request(1);
        log.info("data: {}", value);
    }

    @Override
    protected void hookOnComplete() {
        super.hookOnComplete();
    }

    @Override
    protected void hookOnCancel() {
        super.hookOnCancel();
    }

    @Override
    protected void hookOnError(Throwable throwable) {
        super.hookOnError(throwable);
    }

    @Override
    protected void hookFinally(SignalType type) {
        super.hookFinally(type);
    }
}
