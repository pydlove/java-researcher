package com.aiocloud.gateway.router.config;

import cn.hutool.core.util.BooleanUtil;
import com.aiocloud.gateway.router.access.AccessFilterChain;
import com.aiocloud.gateway.router.access.AccessPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * @description: AuthenticationCheck.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-31 17:04
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationCheck {

    private final AccessFilterChain accessFilterChain;

    /**
     * 是否可以访问
     *
     * @param: request
     * @return: boolean
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-08 14:13
     * @since 1.0.0
     */
    public boolean isAccess(ServerRequest request) {

        // 这里支持白名单、黑名单、token 校验的方式
        AccessPermission accessPermission = accessFilterChain.doFilter(request);
        if (BooleanUtil.isTrue(accessPermission.isAllow())) {
            return true;
        }

        if (BooleanUtil.isTrue(accessPermission.isRefuse())) {
            return false;
        }

        return false;
    }
}
