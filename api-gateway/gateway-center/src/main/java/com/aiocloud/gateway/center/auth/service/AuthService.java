package com.aiocloud.gateway.center.auth.service;

import com.aiocloud.gateway.base.common.CommonResponse;
import com.aiocloud.gateway.center.auth.dto.AuthDTO;
import com.aiocloud.gateway.center.auth.vo.AuthVO;

/**
 *
 * @description: AuthService.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-30 14:18 
 */
public interface AuthService {

    CommonResponse<AuthVO> auth(AuthDTO authDTO);

    CommonResponse<AuthVO> refresh();

}
