package com.aiocloud.gateway.cache.conf;

import java.io.InputStream;
import java.util.Properties;

/**
 * @description: ConfigLoader.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-03 17:58
 */
public class ConfigLoader {

    private static final Properties properties = new Properties();

    static {

        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("cache.properties")) {

            if (input == null) {
                throw new RuntimeException("Unable to find cache.properties");
            }

            properties.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Error loading cache.properties", e);
        }
    }

    /**
     * 获取配置属性值
     *
     * @param: key
     * @return: java.lang.String
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-06 11:31
     * @since 1.0.0
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
