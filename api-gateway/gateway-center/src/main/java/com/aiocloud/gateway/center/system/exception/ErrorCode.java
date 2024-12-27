package com.aiocloud.gateway.center.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /**
     * success
     */
    OK(0, "success"),

    /**
     * parameter error
     */
    PARAMETER_ERROR(1001, "parameter error"),

    /**
     * authenticate failed
     */
    AUTHENTICATE_FAILED(1002, "authenticate failed"),

    /**
     * unauthorized
     */
    UNAUTHORIZED(1003, "unauthorized"),

    /**
     * Expired JWT token
     */
    TOKEN_EXPIRED(1004, "Expired JWT token"),

    /**
     * INVALID JWT token
     */
    INVALID_TOKEN(1005, "INVALID JWT token"),

    /**
     * unauthorized to login platform
     */
    INVALID_DRN(1008, "invalid drn"),

    /**
     * request is not supported yet
     */
    UNSUPPORTED_OPERATION(1099, "unsupported operation"),

    /**
     * production environment not found
     */
    PRODUCTION_ENVIRONMENT_NOT_FOUND(10001, "the production environment not found"),

    /**
     * production environment not found
     */
    SERVICE_UNAVAILABLE(10002, "the service is temporarily unavailable"),

    /**
     * production environment not found
     */
    INVALID_PARAMETERS(10003, "invalid parameters"),

    /**
     * production environment not found
     */
    FAILED_SCHEDULE_JOB(10004, "failed schedule job"),

    /**
     * internal server error
     */
    INTERNAL_SERVER_ERROR(1000, "internal server error"),

    TOKEN_IS_NOT_NULL(1100, "token is null"),

    ;


    private final int code;

    private final String msg;
}
