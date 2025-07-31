package com.aiocloud.llm.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 *
 * @description: TestApplication.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-23 14:48 
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class LlmApplication {

    public static void main(String[] args) {
        SpringApplication.run(LlmApplication.class, args);
    }
}
