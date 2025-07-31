package com.aiocloud.gateway.router.server;

import com.aiocloud.gateway.center.system.ServiceCenter;
import com.aiocloud.gateway.core.registry.ServiceInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Objects;

/**
 * @description: HttpUrlSelector.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-24 16:19
 */
@RequiredArgsConstructor
@Component
public class HttpUrlSelector {

    private final ServiceCenter serviceCenter;

    public static final String HTTP_URL_PREFIX = "/";

    public String getTargetUrl(ServerRequest request) {

        // 解析请求 url 获取 context-path
        String contextPath = request.uri().getPath();
        contextPath = Objects.nonNull(contextPath) && contextPath.length() > 0 ? contextPath : "/";

        if (contextPath.length() > 0 && contextPath.contains(HTTP_URL_PREFIX)) {
            contextPath = contextPath.substring(1);
            contextPath = contextPath.substring(0, contextPath.indexOf("/"));
        }

        // 根据 context-path 从注册中心获取服务的 ip 和端口
        ServiceInstance serviceInfo = serviceCenter.getServiceInfo(contextPath);
        if (Objects.isNull(serviceInfo)) {
            return null;
        }

        return serviceInfo.getAddress() + request.path();
    }
}
