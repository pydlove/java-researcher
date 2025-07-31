package com.aiocloud.seata.xa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 * @description: SeataXAApplication.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-23 14:48
 */
@EnableJpaRepositories(basePackages = "com.aiocloud.seata.xa")
@SpringBootApplication
public class SeataXAApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataXAApplication.class, args);
    }
}
