package com.aiocloud.gateway.core.registry;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @description: ServiceInstance.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-25 17:29 
 */
@Data
@AllArgsConstructor
public class ServiceInstance {

    private String name;
    private String address;
}
