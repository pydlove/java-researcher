package com.aiocloud.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import java.util.Stack;

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
public class TaggerTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaggerTestApplication.class, args);
    }
}
