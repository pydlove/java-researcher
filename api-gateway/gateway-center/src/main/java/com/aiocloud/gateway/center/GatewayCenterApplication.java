package com.aiocloud.gateway.center;

import com.aiocloud.gateway.center.router.service.RouterRegisterService;
import com.aiocloud.gateway.core.registry.ServiceRegistryClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * @description: ApiGatewayApplication
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-20 11:31
 */
@Slf4j
@SpringBootApplication
public class GatewayCenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayCenterApplication.class, args);
	}

	@PostConstruct
	public void init() {
	}
}
