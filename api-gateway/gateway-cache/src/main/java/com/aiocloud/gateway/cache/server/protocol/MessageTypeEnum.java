package com.aiocloud.gateway.cache.server.protocol;

/**
 *
 * @description: MessageTypeEnum.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2025-01-06 13:59 
 */
public enum MessageTypeEnum {

    REQUEST_SET((byte) 1),
    REQUEST_GET((byte) 2),
    PING((byte) 3),
    PONG((byte) 4),
    EMPTY((byte) 5)
    ;

    private byte type;

    MessageTypeEnum(byte type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static MessageTypeEnum get(byte type) {
        
        for (MessageTypeEnum value : values()) {
            if (value.type == type) {
                return value;
            }
        }

        throw new RuntimeException("unsupported type: " + type);
    }
}
