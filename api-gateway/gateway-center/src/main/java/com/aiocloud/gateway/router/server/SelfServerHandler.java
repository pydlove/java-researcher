package com.aiocloud.gateway.router.server;

import cn.hutool.core.util.BooleanUtil;
import com.aiocloud.gateway.base.ApplicationContextProvider;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.condition.PatternsRequestCondition;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

/**
 * @description: ServerHandler.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-24 15:53
 */
@Slf4j
public class SelfServerHandler implements ServerHandler {


    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final ApplicationContextProvider applicationContextProvider;
    private final String gatewayServiceName;

    public SelfServerHandler(RequestMappingHandlerMapping requestMappingHandlerMapping, ApplicationContextProvider applicationContextProvider, String gatewayServiceName) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.applicationContextProvider = applicationContextProvider;
        this.gatewayServiceName = gatewayServiceName;
    }

    public Mono<ServerResponse> handleForwardRequest(ServerRequest request) {

        String path = request.path();
        String finalPath = path.substring(gatewayServiceName.length() + 1);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();

            Set<PathPattern> patterns = patternsCondition.getPatterns();
            if (BooleanUtil.isFalse(patterns.stream().anyMatch(pattern -> pattern.getPatternString().equals(finalPath)))) {
                continue;
            }

            return request.bodyToMono(byte[].class)
                    .flatMap(body -> {

                        return accessSelfMethods(entry, body);

                    });
        }

        return Mono.empty();
    }

    /**
     * 访问注册中心自己的方法
     *
     * @param: entry
     * @param: body
     * @return: reactor.core.publisher.Mono<org.springframework.web.reactive.function.server.ServerResponse>
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-26 18:18
     * @since 1.0.0
     */
    private Mono<ServerResponse> accessSelfMethods(Map.Entry<RequestMappingInfo, HandlerMethod> entry, byte[] body) {

        HandlerMethod handlerMethod = entry.getValue();
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        Class<?> beanType = handlerMethod.getBeanType();

        String beanName = handlerMethod.getBean().toString();
        Object bean = applicationContextProvider.getBean(beanName);

        Method method = handlerMethod.getMethod();

        Object[] args = new Object[methodParameters.length];

        for (int i = 0; i < methodParameters.length; i++) {
            MethodParameter parameter = methodParameters[i];
            Class<?> parameterType = parameter.getParameterType();

            String param = new String(body, StandardCharsets.UTF_8);
            args[i] = JSONObject.parseObject(param, parameterType);
        }

        Class<?> declaringClass = method.getDeclaringClass();
        if (!declaringClass.isInstance(bean)) {
            return Mono.error(new IllegalArgumentException("Bean is not an instance of the method's declaring class"));
        }

        Object result = ReflectionUtils.invokeMethod(method, bean, args);

        return ServerResponse.ok().body(BodyInserters.fromValue(result));
    }
}
