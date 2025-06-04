package com.aiocloud.twopc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource.secondary")
public class SecondaryDatasource {

    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private Boolean jta;
}
