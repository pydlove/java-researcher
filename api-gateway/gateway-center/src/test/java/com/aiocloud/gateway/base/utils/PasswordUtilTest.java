package com.aiocloud.gateway.base.utils;

/**
 *
 * @description: PasswordUtilTest.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-31 14:29 
 */
class PasswordUtilTest {

    public static void main(String[] args) {
        String encodePassword = PasswordUtil.encodePassword("123456");
        System.out.println(encodePassword);
    }
}