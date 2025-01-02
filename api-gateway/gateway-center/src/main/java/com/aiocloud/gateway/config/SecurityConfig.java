package com.aiocloud.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * @description: SecurityConfig.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-31 11:51
 */
@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    /**
     * 放行普通请求
     *
     * @param: http
     * @return: org.springframework.security.web.SecurityFilterChain
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-31 13:50
     * @since 1.0.0
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf()
                .disable()
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    /**
     * 放行响应式请求
     *
     * @param: http
     * @return: org.springframework.security.web.server.SecurityWebFilterChain
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-31 13:49
     * @since 1.0.0
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {

        http.csrf()
                .disable()
                .authorizeExchange((authorize) -> authorize
                        .anyExchange().permitAll()
                );

        return http.build();
    }
}




