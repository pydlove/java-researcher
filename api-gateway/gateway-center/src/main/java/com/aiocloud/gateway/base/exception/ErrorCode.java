package com.aiocloud.gateway.base.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /**
     * success
     */
    OK(0, "success"),

    USER_OR_PASSWORD_ERROR(1001, "用户或者密码有误"),
    INVALID_REFRESH_TOKEN(1002, "Refresh token 无效"),

    ;


    private final int code;

    private final String msg;
}
