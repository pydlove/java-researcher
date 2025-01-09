package com.aiocloud.gateway.cache.conf;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

/**
 * @description: SystemProperties.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-06 9:41
 */
public class SystemProperties {

    private static final Logger log = LoggerFactory.getLogger(SystemProperties.class);

    @Prop(value = "cache.server.host", defaultValue = "127.0.0.1")
    public static String serverHost;

    @Prop(value = "cache.server.port", defaultValue = "8080")
    public static Integer serverPort;

    static {
        loadConfigProperties(new SystemProperties());
    }

    /**
     * 加载配置属性值到对应的属性上
     *
     * @param: systemProperties
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-06 11:15
     * @since 1.0.0
     */
    private static void loadConfigProperties(SystemProperties systemProperties) {

        for (Field field : SystemProperties.class.getDeclaredFields()) {

            if (field.isAnnotationPresent(Prop.class)) {

                Prop propAnnotation = field.getAnnotation(Prop.class);
                String configKey = propAnnotation.value();
                String defaultValue = propAnnotation.defaultValue();
                String configValue = Optional.ofNullable(ConfigLoader.getProperty(configKey)).orElse(defaultValue);

                if (Objects.isNull(configValue)) {
                    continue;
                }

                try {

                    field.setAccessible(true);
                    if (field.getType() == String.class) {
                        field.set(systemProperties, configValue);
                    } else if (field.getType() == int.class || field.getType() == Integer.class) {
                        field.set(systemProperties, Integer.parseInt(configValue));
                    } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        field.set(systemProperties, Boolean.parseBoolean(configValue));
                    } else if (field.getType() == long.class || field.getType() == Long.class) {
                        field.set(systemProperties, Long.parseLong(configValue));
                    } else if (field.getType() == double.class || field.getType() == Double.class) {
                        field.set(systemProperties, Double.parseDouble(configValue));
                    } else if (field.getType() == float.class || field.getType() == Float.class) {
                        field.set(systemProperties, Float.parseFloat(configValue));
                    } else {
                        throw new IllegalArgumentException("Unsupported field type: " + field.getType());
                    }

                } catch (Exception ex) {
                    log.error("load config properties error, cause by:", ex);
                }
            }
        }
    }


    public static void main(String[] args) {

        System.out.println(SystemProperties.serverPort);
    }
}
