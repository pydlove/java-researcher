package com.aiocloud.gateway.core.base;

import cn.hutool.core.util.StrUtil;
import org.springframework.http.HttpMethod;

/**
 * @description: HttpMethodSelector.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-24 11:32
 */
public class HttpMethodSelector {

    private static class SingletonHolder {
        private static final HttpMethodSelector INSTANCE = new HttpMethodSelector();
    }

    public static HttpMethodSelector getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private HttpMethodSelector() {
    }

    /**
     * getHttpMethod
     *
     * @param: method
     * @return: org.springframework.http.HttpMethod
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-24 11:35
     * @since 1.0.0
     */
    public HttpMethod selectHttpMethod(String method) {

        if (StrUtil.isEmpty(method)) {
            return null;
        }

        switch (method) {
            case "GET":
                return HttpMethod.GET;
            case "POST":
                return HttpMethod.POST;
            case "PUT":
                return HttpMethod.PUT;
            case "DELETE":
                return HttpMethod.DELETE;
            case "PATCH":
                return HttpMethod.PATCH;
            case "HEAD":
                return HttpMethod.HEAD;
            case "OPTIONS":
                return HttpMethod.OPTIONS;
            default:
                return null;
        }
    }

    public static boolean checkIsPostOrPut(HttpMethod httpMethod) {
        return HttpMethod.POST.equals(httpMethod) || HttpMethod.PUT.equals(httpMethod);
    }
}
