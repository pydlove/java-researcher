package com.aiocloud.gateway.router.access;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.aiocloud.gateway.base.utils.JwtUtil;
import com.aiocloud.gateway.config.SystemConfig;
import com.aiocloud.gateway.config.SystemJwtConfig;
import com.aiocloud.gateway.constant.SystemConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

/**
 *
 * @description: TokenFilter.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-01-08 14:10 
 */
@Slf4j
@Component
@FilterOrder(3)
public class TokenFilter implements AccessFilter {

    private final SystemConfig systemConfig;
    private final AntPathMatcher antPathMatcher;
    private final SystemJwtConfig systemJwtConfig;

    public TokenFilter(SystemConfig systemConfig, SystemJwtConfig systemJwtConfig) {
        this.systemConfig = systemConfig;
        this.systemJwtConfig = systemJwtConfig;
        this.antPathMatcher =  new AntPathMatcher();
    }

    @Override
    public AccessPermission doFilter(ServerRequest request) {

        // 获取 token
        String token = getTokenFromHeader(request);
        if (token == null) return new AccessPermission(AccessPermission.REFUSE);

        // 签名验证
        if (BooleanUtil.isFalse(JwtUtil.verifySignature(token))) {
            return new AccessPermission(AccessPermission.REFUSE);
        }

        // 过期时间检查
        if (BooleanUtil.isTrue(JwtUtil.isTokenExpired(token))) {
            return new AccessPermission(AccessPermission.REFUSE);
        }

        // Issuer (iss 发行者) 和 Audience (aud 受众) 检查
        if (BooleanUtil.isFalse(JwtUtil.validateIssuer(token, systemJwtConfig.getIssuer())) || BooleanUtil.isFalse(JwtUtil.validateAudience(token))) {
            return new AccessPermission(AccessPermission.REFUSE);
        }

        // 用户是否有权限访问接口（后续扩展）

        return new AccessPermission(AccessPermission.ALLOW);
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
