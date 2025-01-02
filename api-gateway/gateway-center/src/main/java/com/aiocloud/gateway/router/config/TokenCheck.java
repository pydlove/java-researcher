package com.aiocloud.gateway.router.config;

import cn.hutool.core.util.BooleanUtil;
import com.aiocloud.gateway.base.utils.JwtUtil;
import com.aiocloud.gateway.constant.SystemConstant;
import com.aiocloud.gateway.router.access.AccessFilterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * @description: TokenCheck.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-31 17:04
 */
@RequiredArgsConstructor
@Component
public class TokenCheck {

    private final AccessFilterChain accessFilterChain;

    public boolean isTokenValid(ServerRequest request) {

        // 需要对一些接口进行放行，这里要支持白名单、黑名单的方式
        if (BooleanUtil.isFalse(accessFilterChain.doFilter(request))) {
            return false;
        }

        // 获取 token
        HttpHeaders httpHeaders = request.headers().asHttpHeaders();
        httpHeaders.get(SystemConstant.X_AUTH_TOKEN);

        // 签名验证

        // 过期时间检查

        // Issuer (iss) 和 Audience (aud) 检查

        // 用户是否有权限访问接口（后续扩展）

        return true;
    }
}
