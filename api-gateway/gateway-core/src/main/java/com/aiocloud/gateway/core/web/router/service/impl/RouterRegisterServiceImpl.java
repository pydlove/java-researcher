package com.aiocloud.gateway.core.web.router.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.aiocloud.gateway.core.web.router.dto.RouterRegisterDTO;
import com.aiocloud.gateway.core.web.router.po.RouterRegisterPO;
import com.aiocloud.gateway.core.web.router.service.RouterRegisterService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @description: RouterRegisterServiceImpl.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-20 14:05 
 */
@Service
public class RouterRegisterServiceImpl implements RouterRegisterService {

    private final Map<String, RouterRegisterPO> services = new HashMap<>();

    @Override
    public Mono<ServerSentEvent<String>> registerService(RouterRegisterDTO routerRegister) {

        RouterRegisterPO routerRegisterPO = BeanUtil.copyProperties(routerRegister, RouterRegisterPO.class);
        services.put(routerRegisterPO.getServiceName(), routerRegisterPO);

        return null;
    }
}
