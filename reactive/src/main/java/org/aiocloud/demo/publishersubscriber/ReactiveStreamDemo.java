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
public class ReactiveStreamDemo {

    public static void main(String[] args) {

        try (SubmissionPublisher<String> publisher = new SubmissionPublisher<>()) {

            CustomSubscriber customSubscriber1 = new CustomSubscriber("AAA");
            CustomSubscriber customSubscriber2 = new CustomSubscriber("BBB");

            publisher.subscribe(customSubscriber1);
            publisher.subscribe(customSubscriber2);

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