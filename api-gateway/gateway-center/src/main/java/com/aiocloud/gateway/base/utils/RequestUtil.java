package com.aiocloud.gateway.base.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 *
 * @description: RequestTool.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-30 15:32 
 */
public class RequestUtil {

    /**
     * getHttpServletRequest
     * 
     * @since 1.0.0
     * 
     * @return: jakarta.servlet.http.HttpServletRequest 
     * @author: panyong 
     * @version: 1.0.0 
     * @createTime: 2024-11-30 9:56 
     */  
    public static HttpServletRequest getHttpServletRequest() {
        
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        if (Objects.isNull(servletRequestAttributes)) {
            return null;
        }

        return servletRequestAttributes.getRequest();
    }
}
