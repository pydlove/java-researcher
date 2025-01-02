package com.aiocloud.gateway.center.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 *
 * @description: AuthVO.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-31 14:31 
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthVO {

    private String accessToken;

    private String refreshToken;
}
