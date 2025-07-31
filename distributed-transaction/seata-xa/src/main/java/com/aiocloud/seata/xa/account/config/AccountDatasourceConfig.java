package com.aiocloud.seata.xa.account.config;

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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.aiocloud.seata.xa.account.entity",
        entityManagerFactoryRef = "accountEntityManagerFactory",
        transactionManagerRef = "accountTransactionManager"
)
public class AccountDatasourceConfig {

    @Primary
    @Bean(name = "accountDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource accountDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public DataSourceProxy accountDataSourceProxy(@Qualifier("accountDataSource") DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

    @Bean(name = "accountEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean accountEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(accountDataSourceProxy(accountDataSource()))
                .packages("com.aiocloud.seata.xa.entity")
                .persistenceUnit("account")
                .properties(jpaProperties())
                .build();
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        return props;
    }
    @Bean(name = "accountTransactionManager")
    public PlatformTransactionManager accountTransactionManager(
            @Qualifier("accountEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}