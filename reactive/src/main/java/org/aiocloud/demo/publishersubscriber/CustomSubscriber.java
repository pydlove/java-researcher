package org.aiocloud.demo.publishersubscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Flow;

/**
 *
 * @description:  
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-17 16:19
 */
public class CustomSubscriber implements Flow.Subscriber<String> {

    private static final Logger log = LoggerFactory.getLogger(CustomSubscriber.class);

    private Flow.Subscription subscription;

    private final String subjectFlag;

    public CustomSubscriber(String subjectFlag) {
        this.subjectFlag = subjectFlag;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        log.info("subjectFlag: {}, 订阅：{}", subjectFlag, subscription);

        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(String item) {

        log.info("subjectFlag: {}, 订阅获取的数据：{}", subjectFlag, item);

        // 背压模式
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {
        
        log.info("订阅数据完成");
    }
}
