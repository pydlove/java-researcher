package com.aiocloud.twopc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 * @description: TestApplication.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-23 14:48
 */
@EnableJpaRepositories(
        basePackages = "com.aiocloud.twopc.repository",
        entityManagerFactoryRef = "primaryEntityManagerFactory"
)
@SpringBootApplication
public class TwoPCApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwoPCApplication.class, args);
    }
}
