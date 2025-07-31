package com.aiocloud.gateway.center.auth.controller;

import com.aiocloud.gateway.base.common.CommonResponse;
import com.aiocloud.gateway.center.auth.dto.AuthDTO;
import com.aiocloud.gateway.center.auth.service.AuthService;
import com.aiocloud.gateway.center.auth.vo.AuthVO;
import com.aiocloud.gateway.core.registry.ServiceInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: AuthController.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-30 14:12
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/do")
    public CommonResponse<AuthVO> auth(@RequestBody AuthDTO authDTO) {
        return authService.auth(authDTO);
    }

    @PostMapping("/refresh")
    public CommonResponse<AuthVO> refresh() {
        return authService.refresh();
    }
}
