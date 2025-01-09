package com.aiocloud.gateway.router.access;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.aiocloud.gateway.config.SystemConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 *
 * @description: WhitelistFilter.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-02 16:39
 */
@Component
@FilterOrder(2)
public class WhitelistFilter implements AccessFilter {

    private final SystemConfig systemConfig;

    private final AntPathMatcher antPathMatcher;

    public WhitelistFilter(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
        this.antPathMatcher =  new AntPathMatcher();
    }

    @Override
    public AccessPermission doFilter(ServerRequest request) {

        String whiteList = systemConfig.getWhiteList();
        if (StrUtil.isEmpty(whiteList)) {
            return new AccessPermission(AccessPermission.UNCONFIRMED);
        }

        String[] whiteListArray = whiteList.split(",");
        if (ArrayUtil.isEmpty(whiteListArray)) {
            return new AccessPermission(AccessPermission.UNCONFIRMED);
        }

        for (String accessRule : whiteListArray) {

            if (StrUtil.isEmpty(accessRule)) {
                continue;
            }

            // 通过正则判断是否满足白名单规则
            String path = request.uri().getPath();
            if (antPathMatcher.match(accessRule.trim(), path)) {
                return new AccessPermission(AccessPermission.ALLOW);
            }
        }

        return new AccessPermission(AccessPermission.UNCONFIRMED);
    }

    public static void main(String[] args) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        System.out.println(antPathMatcher.match("/test-service/test/white", "/gateway-service/register/do"));
        System.out.println(antPathMatcher.match("/gateway-service/**", "/gateway-service/register/do"));
    }
}
