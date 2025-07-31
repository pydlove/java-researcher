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
 * @description: Blacklist.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-01-02 16:40 
 */
@Component
@FilterOrder(1)
public class Blacklist implements AccessFilter {

    private final SystemConfig systemConfig;

    private final AntPathMatcher antPathMatcher;

    public Blacklist(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
        this.antPathMatcher =  new AntPathMatcher();
    }

    @Override
    public AccessPermission doFilter(ServerRequest request) {

        String blackList = systemConfig.getBlackList();
        if (StrUtil.isEmpty(blackList)) {
            return new AccessPermission(AccessPermission.UNCONFIRMED);
        }

        String[] blackListArray = blackList.split(",");
        if (ArrayUtil.isEmpty(blackListArray)) {
            return new AccessPermission(AccessPermission.UNCONFIRMED);
        }

        for (String accessRule : blackListArray) {

            if (StrUtil.isEmpty(accessRule)) {
                continue;
            }

            // 通过正则判断是否满足黑名单规则
            String path = request.uri().getPath();
            if (antPathMatcher.match(accessRule, path)) {
                return new AccessPermission(AccessPermission.REFUSE);
            }
        }

        return new AccessPermission(AccessPermission.UNCONFIRMED);
    }
}
