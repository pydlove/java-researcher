package org.aiocloud.r2dbc.config;

import org.aiocloud.r2dbc.convert.CustomConverter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * @description:
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-19 14:25
 */
@EnableR2dbcRepositories
@Configuration
public class R2DbcConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public R2dbcCustomConversions conversions() {

        // 把我们的转换器加入进去； 效果新增了我们的 Converter
        return R2dbcCustomConversions.of(MySqlDialect.INSTANCE, new CustomConverter());
    }
}
