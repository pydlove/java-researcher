package com.aiocloud.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource.druid")
public class MysqlDataSource {

    private String username;
    private String password;
    private String url;
    private String name;
    private String driverClassName;
    private boolean encrypt;
    private int initialSize;
    private int minIdle;
    private int maxActive;
    private int maxWait;
    private int timeBetweenEvictionRunsMillis;
    private int minEvictableIdleTimeMillis;
    private String validationQuery;
    private boolean testWhileIdle;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean poolPreparedStatements;
    private int maxPoolPreparedStatementPerConnectionSize;
}
