package com.aiocloud.twopc.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import lombok.RequiredArgsConstructor;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @description: JtaConfig.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-06-04 11:32 
 */
@RequiredArgsConstructor
@Configuration
public class JtaConfig {

    private final PrimaryDatasource primaryDatasource;
    private final SecondaryDatasource secondaryDatasource;

    @Primary
    @Bean
    public DataSource primaryDataSource() {

        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("primaryDataSource");
        dataSource.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
        Properties properties = new Properties();
        properties.put("url", primaryDatasource.getUrl());
        properties.put("user", primaryDatasource.getUsername());
        properties.put("password", primaryDatasource.getPassword());
        dataSource.setXaProperties(properties);
        return dataSource;
    }

    @Bean
    public DataSource secondaryDataSource() {

        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("secondaryDataSource");
        dataSource.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
        Properties properties = new Properties();
        properties.put("url", secondaryDatasource.getUrl());
        properties.put("user", secondaryDatasource.getUsername());
        properties.put("password", secondaryDatasource.getPassword());
        dataSource.setXaProperties(properties);
        return dataSource;
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(primaryDataSource());
        em.setPackagesToScan("com.aiocloud.twopc.entity", "com.aiocloud.twopc.repository.mysql");
        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");
        em.setJpaVendorAdapter(adapter);

        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform");

        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(secondaryDataSource());
        em.setPackagesToScan("com.aiocloud.twopc.entity");

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");
        em.setJpaVendorAdapter(adapter);

        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform");

        em.setJpaPropertyMap(properties);

        return em;
    }
}
