package com.aiocloud.gateway.core.config;

//import com.aiocloud.gateway.core.registry.ServiceRegistryClient;
import com.aiocloud.gateway.core.registry.ServiceRegistryClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @description: ServiceRegistryAutoConfiguration.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-25 14:37 
 */
@ComponentScan(basePackages = "com.aiocloud.gateway.core")
@Configuration
@EnableConfigurationProperties(ServiceRegistryConfig.class)
public class ServiceRegistryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ServiceRegistryConfig serviceRegistryConfig() {
        return new ServiceRegistryConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceRegistryClient serviceRegistryClient() {
        return new ServiceRegistryClient();
    }
}

