package com.aiocloud.gateway.router.access;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @description: AccessFilterChain.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-31 17:43
 */
@Component
public class AccessFilterChain implements AccessFilter {

    private List<AccessFilter> accessFilters;

    private final ApplicationContext applicationContext;

    public AccessFilterChain(ApplicationContext applicationContext) {
        this.accessFilters = new ArrayList<>();
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {

        // 获取所有 AccessFilter 的子类
        Map<String, AccessFilter> accessFilterMap = applicationContext.getBeansOfType(AccessFilter.class);
        if (CollUtil.isEmpty(accessFilterMap)) {
            return;
        }

        // 这里可以基于注解实现链式排序
        accessFilterMap.forEach((k, accessFilter) -> {

            if (accessFilter instanceof AccessFilterChain) {
                return;
            }

            this.accessFilters.add(accessFilter);
        });

        // 基于注解排序
        this.accessFilters.sort(Comparator.comparingInt(filter -> {
            FilterOrder order = filter.getClass().getAnnotation(FilterOrder.class);
            return order != null ? order.value() : Integer.MAX_VALUE;
        }));
    }

    public void add(AccessFilter accessFilter) {
        accessFilters.add(accessFilter);
    }

    /**
     * 遍历链式所有的过滤，如果都成功，则返回 true，认为通过检测
     *
     * @return: boolean
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-31 18:13
     * @since 1.0.0
     */
    @Override
    public AccessPermission doFilter(ServerRequest request) {

        AccessPermission accessPermission = new AccessPermission(AccessPermission.REFUSE);
        for (AccessFilter accessFilter : accessFilters) {
            accessPermission = accessFilter.doFilter(request);
            if (BooleanUtil.isFalse(accessPermission.isUnconfirmed())) {
                break;
            }
        }

        return accessPermission;
    }
}
