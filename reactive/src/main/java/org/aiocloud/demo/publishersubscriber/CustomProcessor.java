package org.aiocloud.demo.publishersubscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 *
 * @description:  
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-17 16:57
 */
public class CustomProcessor extends SubmissionPublisher<String> implements Flow.Processor<String, String> {

    private static final Logger log = LoggerFactory.getLogger(CustomProcessor.class);

    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(String item) {
        log.info("CustomProcessor get message: {}", item);

        // 加工数据
        String finalItem = "加工了：" + item;
        submit(finalItem);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }
}
