package org.aiocloud.demo.publishersubscriber;

import java.util.concurrent.SubmissionPublisher;

/**
 *
 * @description:  
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-17 14:53
 */
public class ReactiveStreamDemo1 {

    public static void main(String[] args) {

        try (SubmissionPublisher<String> publisher = new SubmissionPublisher<>()) {

            CustomProcessor customProcessor = new CustomProcessor();

            CustomSubscriber customSubscriber = new CustomSubscriber("AAA");

            customProcessor.subscribe(customSubscriber);
            publisher.subscribe(customProcessor);

            for (int i = 0; i < 10; i++) {
                publisher.submit(String.valueOf(i));
            }

        }

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}