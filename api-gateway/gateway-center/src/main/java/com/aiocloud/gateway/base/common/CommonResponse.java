package com.aiocloud.gateway.base.common;

import com.aiocloud.gateway.base.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @description: CommonResponse.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-26 15:31 
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    public static final CommonResponse<Void> NO_CONTENT = new CommonResponse<>();

    private static final String SERVICE_NAME = "api-gateway";

    private ErrorResponse<?> error;

    private T data;

    public CommonResponse(T value) {
        data = value;
    }

    public CommonResponse(ErrorCode errorCode, T value) {
        error = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMsg())
                .service(SERVICE_NAME)
                .detail(value)
                .build();
    }

    @Data
    @Builder
    public static class ErrorResponse<T> {
        private String service;

        private int code;

        private String message;

        private T detail;
    }

}

