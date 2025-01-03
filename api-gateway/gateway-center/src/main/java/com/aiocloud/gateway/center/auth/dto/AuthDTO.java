package com.aiocloud.gateway.center.auth.dto;

import lombok.Data;

/**
 *
 * @description: AuthDTO.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-30 14:16 
 */
@Data
public class AuthDTO {

    private String username;

    private String password;

    /**
     * 受众信息（用于 token 校验）
     */
    private String audience;
}
