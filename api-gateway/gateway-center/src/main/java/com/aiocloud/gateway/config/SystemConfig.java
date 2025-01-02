package com.aiocloud.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 * @description: SystemConfig.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-27 10:51 
 */
@Primary
@Data
@Component
@ConfigurationProperties(prefix = "system")
public class SystemConfig {

    private String blackList;
    private String whiteList;
}
