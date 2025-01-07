package com.aiocloud.gateway.cache.base.utils;

import cn.ipokerface.snowflake.SnowflakeIdGenerator;

/**
 * @description: SnowflakeIdGeneratorUtil.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-07 14:16
 */
public class SnowflakeIdGeneratorUtil {

    private static final SnowflakeIdGenerator idGenerator;

    static {
        // 初始化 SnowflakeIdGenerator，设置数据中心 ID 和工作节点 ID
        idGenerator = new SnowflakeIdGenerator(1, 1);
    }

    /**
     * 生产 id
     *
     * @return: long
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-07 14:17
     * @since 1.0.0
     */
    public static long generateId() {
        return idGenerator.nextId();
    }
}
