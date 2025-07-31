package com.aiocloud.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 * @description: ServiceConfig.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-27 10:51 
 */
@Primary
@Data
@Component
@ConfigurationProperties(prefix = "service.load.balance")
public class ServiceConfig {

    private String strategy;
}
