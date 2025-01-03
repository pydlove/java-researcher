package com.aiocloud.gateway.router.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import com.aiocloud.gateway.base.utils.JwtUtil;
import com.aiocloud.gateway.config.SystemJwtConfig;
import com.aiocloud.gateway.constant.SystemConstant;
import com.aiocloud.gateway.router.access.AccessFilterChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

/**
 * @description: TokenCheck.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-31 17:04
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TokenCheck {

    private final AccessFilterChain accessFilterChain;
    private final SystemJwtConfig systemJwtConfig;

    public boolean isTokenValid(ServerRequest request) {

        // 需要对一些接口进行放行，这里要支持白名单、黑名单、免 token 校验的方式
        if (BooleanUtil.isFalse(accessFilterChain.doFilter(request))) {
            return false;
        }

        // 获取 token
        String token = getTokenFromHeader(request);
        if (token == null) return false;

        // 签名验证
        if (BooleanUtil.isFalse(JwtUtil.verifySignature(token))) {
            return false;
        }

        // 过期时间检查
        if (BooleanUtil.isTrue(JwtUtil.isTokenExpired(token))) {
            return false;
        }

        // Issuer (iss 发行者) 和 Audience (aud 受众) 检查
        if (BooleanUtil.isFalse(JwtUtil.validateIssuer(token, systemJwtConfig.getIssuer())) || BooleanUtil.isFalse(JwtUtil.validateAudience(token))) {
            return false;
        }

        // 用户是否有权限访问接口（后续扩展）

        return true;
    }

    /**
     * 从 header 中获取 token
     *
     * @param: request
     * @return: java.lang.String
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-02 20:40
     * @since 1.0.0
     */
    private String getTokenFromHeader(ServerRequest request) {

        HttpHeaders httpHeaders = request.headers().asHttpHeaders();
        List<String> tokenHeaders = httpHeaders.get(SystemConstant.X_AUTH_TOKEN);
        if (CollUtil.isEmpty(tokenHeaders)) {

            log.error("Received request for path: {} token is empty", request.path());
            return null;
        }

        return tokenHeaders.get(0);
    }
}
