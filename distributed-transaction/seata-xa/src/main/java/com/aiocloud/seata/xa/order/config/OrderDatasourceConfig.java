package com.aiocloud.seata.xa.order.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.aiocloud.seata.xa.order.entity",
        entityManagerFactoryRef = "orderEntityManagerFactory",
        transactionManagerRef = "orderTransactionManager"
)
public class OrderDatasourceConfig {

    @Bean(name = "orderDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.order")
    public DataSource orderDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public DataSourceProxy orderDataSourceProxy(@Qualifier("orderDataSource") DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

    @Bean(name = "orderEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean orderEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(orderDataSourceProxy(orderDataSource()))
                .packages("com.example.entity.order")
                .persistenceUnit("order")
                .build();
    }

    @Bean(name = "orderTransactionManager")
    public PlatformTransactionManager orderTransactionManager(
            @Qualifier("orderEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
