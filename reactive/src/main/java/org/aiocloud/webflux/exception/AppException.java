package org.aiocloud.webflux.exception;

import java.io.Serializable;


/**
 * 自定义应用异常类
 *
 * @description: 自定义应用异常类，包含错误码和消息模板
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-19 11:35
 */
public class AppException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int errorCode;

    /**
     * 默认构造函数
     */
    public AppException() {
        super();
        this.errorCode = 0; // 默认错误码
    }

    /**
     * 构造函数，带错误消息
     *
     * @param message 错误消息
     */
    public AppException(String message) {
        super(message);
        this.errorCode = 0; // 默认错误码
    }

    /**
     * 构造函数，带错误消息和原因
     *
     * @param message 错误消息
     * @param cause   原因
     */
    public AppException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = 0; // 默认错误码
    }

    /**
     * 构造函数，带错误码和消息
     *
     * @param errorCode 错误码
     * @param message   错误消息
     */
    public AppException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数，带错误码、消息和原因
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @param cause     原因
     */
    public AppException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "AppException{" +
                "errorCode=" + errorCode +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
