package com.aiocloud.gateway.mysql.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @description: LoginUserPO.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-31 13:54 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginUserPO extends BasePO implements Serializable {

    private Long id;

    private String username;

    private String password;
    private static final long serialVersionUID = 1L;
}