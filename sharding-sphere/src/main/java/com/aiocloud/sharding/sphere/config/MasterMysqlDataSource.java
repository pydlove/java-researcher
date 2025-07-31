package com.aiocloud.sharding.sphere.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Data
@Component
@ConfigurationProperties(prefix = "spring.shardingsphere.datasource.ds0")
public class MasterMysqlDataSource {

    private String username;
    private String password;
    private String url;
    private String driverClassName;
}
