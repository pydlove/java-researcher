package com.aiocloud.gateway.config;

import cn.hutool.core.util.BooleanUtil;
import com.aiocloud.gateway.base.utils.DruidPasswordEncryptorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @description: DataSourceConfig.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-31 11:21
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class DataSourceConfig {

    private final MysqlDataSource mysqlDataSource;

    @Bean
    public DataSource dataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        try {
            String username =
                    BooleanUtil.isTrue(mysqlDataSource.isEncrypt()) ? DruidPasswordEncryptorUtil.decrypt(mysqlDataSource.getUsername()) : mysqlDataSource.getUsername();

            String password =
                    BooleanUtil.isTrue(mysqlDataSource.isEncrypt()) ? DruidPasswordEncryptorUtil.decrypt(mysqlDataSource.getPassword()) : mysqlDataSource.getPassword();

            dataSource.setDriverClassName(mysqlDataSource.getDriverClassName());
            dataSource.setUrl(mysqlDataSource.getUrl());
            dataSource.setUsername(username);
            dataSource.setPassword(password);

        } catch (Exception ex) {
            log.error("init datasource error, cause by:", ex);
        }

        return dataSource;
    }
}
