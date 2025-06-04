package com.aiocloud.sharding.sphere.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.apache.shardingsphere.infra.config.algorithm.AlgorithmConfiguration;
import org.apache.shardingsphere.readwritesplitting.api.ReadwriteSplittingRuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.api.rule.ReadwriteSplittingDataSourceRuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.api.strategy.StaticReadwriteSplittingStrategyConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @description: 分库分表配置
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-06-03 15:32
 */
@RequiredArgsConstructor
@Configuration
public class ShardingSphereConfig {

    private final MasterMysqlDataSource masterMysqlDataSource;
    private final SlaveMysqlDataSource slaveMysqlDataSource;

    @Bean
    public DataSource dataSource() throws SQLException {

        Map<String, DataSource> datasourceMap = new HashMap<>();

        datasourceMap.put("master",
                createDataSource(
                        masterMysqlDataSource.getDriverClassName(),
                        masterMysqlDataSource.getUrl(),
                        masterMysqlDataSource.getUsername(),
                        masterMysqlDataSource.getPassword()));

        datasourceMap.put("slave0",
                createDataSource(
                        slaveMysqlDataSource.getDriverClassName(),
                        slaveMysqlDataSource.getUrl(),
                        slaveMysqlDataSource.getUsername(),
                        slaveMysqlDataSource.getPassword()));

        // 配置读写分离数据源规则
        ReadwriteSplittingDataSourceRuleConfiguration configuration = new ReadwriteSplittingDataSourceRuleConfiguration(
                "ms",
                new StaticReadwriteSplittingStrategyConfiguration("master", Arrays.asList("slave0")),
                null,
                "round_robin"
        );

        // 配置负载均衡算法
        Map<String, AlgorithmConfiguration> loadBalanceMap = new HashMap<>();
        loadBalanceMap.put("round_robin", new AlgorithmConfiguration("ROUND_ROBIN", new Properties()));

        // 创建读写分离规则配置
        ReadwriteSplittingRuleConfiguration ruleConfiguration = new ReadwriteSplittingRuleConfiguration(
                Collections.singleton(configuration),
                loadBalanceMap
        );

        // 创建数据源
        return ShardingSphereDataSourceFactory.createDataSource(
                datasourceMap,
                Collections.singleton(ruleConfiguration),
                new Properties()
        );
    }

    private DataSource createDataSource(String driverClassName, String url, String username, String password) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(20);
        dataSource.setMinimumIdle(5);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        return dataSource;
    }
}
