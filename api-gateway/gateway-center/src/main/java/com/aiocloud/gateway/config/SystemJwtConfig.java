package com.aiocloud.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 * @description: SystemJwtConfig.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-02 20:51
 */
@Primary
@Data
@Component
@ConfigurationProperties(prefix = "system.jwt")
public class SystemJwtConfig {

    private String issuer;
    private String audience;
}
