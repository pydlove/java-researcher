package com.aiocloud.gateway.center.auth.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.aiocloud.gateway.base.common.CommonResponse;
import com.aiocloud.gateway.base.exception.ErrorCode;
import com.aiocloud.gateway.base.utils.RequestUtil;
import com.aiocloud.gateway.center.auth.dto.AuthDTO;
import com.aiocloud.gateway.center.auth.service.AuthService;
import com.aiocloud.gateway.base.utils.JwtUtil;
import com.aiocloud.gateway.base.utils.PasswordUtil;
import com.aiocloud.gateway.center.auth.vo.AuthVO;
import com.aiocloud.gateway.config.SystemJwtConfig;
import com.aiocloud.gateway.constant.SystemConstant;
import com.aiocloud.gateway.mysql.mapper.LoginUserMapper;
import com.aiocloud.gateway.mysql.po.LoginUserPO;
import com.aiocloud.gateway.router.config.TokenCheck;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @description: AuthServiceImpl.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-30 14:18
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final LoginUserMapper loginUserMapper;
    private final SystemJwtConfig systemJwtConfig;

    @Override
    public CommonResponse<AuthVO> auth(AuthDTO authDTO) {

        String username = authDTO.getUsername();
        String password = authDTO.getPassword();
        String audience = authDTO.getAudience();

        LoginUserPO user = loginUserMapper.findByUsername(username);
        if (user == null) {
            return new CommonResponse(ErrorCode.USER_OR_PASSWORD_ERROR, "用户或者密码有误");
        }

        boolean matches = PasswordUtil.matches(password, user.getPassword());
        if (BooleanUtil.isFalse(matches)) {
            return new CommonResponse(ErrorCode.USER_OR_PASSWORD_ERROR, "用户名或密码错误");
        }

        String accessToken = JwtUtil.generateToken(username, systemJwtConfig.getIssuer(), audience);
        String refreshToken = JwtUtil.generateRefreshToken(username, systemJwtConfig.getIssuer(), audience);

        // audience 缓存管理
        JwtUtil.addAudienceCache(audience);

        return new CommonResponse(new AuthVO(accessToken, refreshToken));
    }

    @Override
    public CommonResponse<AuthVO> refresh() {

        HttpServletRequest httpServletRequest = RequestUtil.getHttpServletRequest();
        String refreshToken = httpServletRequest.getHeader(SystemConstant.X_REFRESH_TOKEN);

        if (JwtUtil.isRefreshTokenValid(refreshToken)) {
            String accessToken = JwtUtil.refreshToken(refreshToken, systemJwtConfig.getIssuer(), systemJwtConfig.getAudience());
            return new CommonResponse<>(new AuthVO(accessToken, refreshToken));
        }

        return new CommonResponse<>(ErrorCode.INVALID_REFRESH_TOKEN, null);
    }
}
