package com.aiocloud.gateway.router.access;

import lombok.Data;

/**
 *
 * @description: AccessPermission.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-01-08 14:43 
 */
@Data
public class AccessPermission {

    public static final int ALLOW = 200;
    public static final int REFUSE = 400;
    public static final int UNCONFIRMED = 0;

    private int status;

    public AccessPermission(int status) {
        this.status = status;
    }

    public boolean isUnconfirmed() {
        return status == UNCONFIRMED;
    }

    public boolean isAllow() {
        return status == ALLOW;
    }

    public boolean isRefuse() {
        return status == REFUSE;
    }
}
