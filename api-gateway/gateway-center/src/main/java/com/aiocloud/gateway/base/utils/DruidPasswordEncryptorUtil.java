package com.aiocloud.gateway.base.utils;

import com.alibaba.druid.filter.config.ConfigTools;

/**
 *
 * @description: DruidPasswordEncryptorUtil.java
 * @copyright: @copyright (c) 2022 
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0 
 * @createTime: 2024-12-31 11:27 
 */
public class DruidPasswordEncryptorUtil {

    public static String encrypt(String value) throws Exception {
        return ConfigTools.encrypt(value);
    }

    public static String decrypt(String value) throws Exception {
        return ConfigTools.decrypt(value);
    }

    public static void main(String[] args) {
        try {
            String password = "123456";
            String encrypted = ConfigTools.encrypt(password);
            System.out.println("Encrypted Password: " + password);
            System.out.println("Encryption Key: " + encrypted);

            String decrypted = ConfigTools.decrypt(encrypted);
            System.out.println("Decrypted Password: " + decrypted);

            String username = "root";
            String usernameEncrypted = ConfigTools.encrypt(username);
            System.out.println("Encrypted Password: " + username);
            System.out.println("Encryption Key: " + usernameEncrypted);

            String usernameDecrypted = ConfigTools.decrypt(encrypted);
            System.out.println("Decrypted Username: " + usernameDecrypted);

        } catch (Exception e) {
        }
    }
}
