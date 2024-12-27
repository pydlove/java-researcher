package com.aiocloud.gateway.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 * @description: ServiceRegistryConfig.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-25 14:12 
 */
@Primary
@Data
@Component
@ConfigurationProperties(prefix = "service.registry")
public class ServiceRegistryConfig {

    private String serviceName;
    private String registryUrl;
    private String serviceUrl;

}
