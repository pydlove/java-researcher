package com.aiocloud.gateway.cache.base.utils;

import com.aiocloud.gateway.cache.model.CacheMessage;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @description: SerializationUtil.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2025-01-08 17:11
 */
public class SerializationUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 反序列化
     *
     * @param: bytes
     * @return: java.lang.Object
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-08 17:13
     * @since 1.0.0
     */
    public static <T> T deserializeObject(byte[] bytes, Class<T> clazz) throws Exception {

        return objectMapper.readValue(bytes, clazz);
    }

    /**
     * 序列化
     *
     * @param: obj
     * @return: byte[]
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-08 17:25
     * @since 1.0.0
     */
    public static byte[] serializeObject(Object obj) throws Exception {

        return objectMapper.writeValueAsBytes(obj);
    }

    public static void main(String[] args) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        CacheMessage originalMessage = new CacheMessage();
        originalMessage.setKey("test");

        System.out.println(Charset.defaultCharset().name());

        byte[] serializedBytes = serializeObject(originalMessage);

        byte[] bytes = new String(serializedBytes).getBytes();

        CacheMessage deserializedMessage = deserializeObject(bytes, CacheMessage.class);

        System.out.println(JSONObject.toJSONString(deserializedMessage));
    }
}
