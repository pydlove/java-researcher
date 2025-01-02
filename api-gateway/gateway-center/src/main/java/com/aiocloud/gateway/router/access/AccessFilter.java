package com.aiocloud.gateway.router.access;

import org.springframework.web.reactive.function.server.ServerRequest;

/**
 *
 * @description: AccessFilter.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-31 17:40 
 */
public interface AccessFilter {

    boolean doFilter(ServerRequest request);
}
