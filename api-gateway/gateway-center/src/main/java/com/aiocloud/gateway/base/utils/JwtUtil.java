package com.aiocloud.gateway.base.utils;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import static com.aiocloud.gateway.constant.SystemConstant.BEARER_PREFIX;

/**
 * @description: JwtUtil.java
 * @copyright: @copyright (c) 2022
 * @company: aiocloud
 * @author: panyong
 * @version: 1.0.0
 * @createTime: 2024-12-31 9:59
 */
@Slf4j
public class JwtUtil {

    private static String secretKey;
    private static long expirationTime = 1000 * 60 * 30;
    private static long refreshTokenExpirationTime = 1000 * 60 * 60 * 24 * 7;

    static {
        generateRandomSecretKey();
    }

    public static String generateToken(String username) {

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public static String generateRefreshToken(String username) {

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public static void generateRandomSecretKey() {

        SecureRandom random = new SecureRandom();
        byte[] key = new byte[256];
        random.nextBytes(key);
        secretKey = Base64.getEncoder().encodeToString(key);

        log.info("generate random secret key: {}", secretKey);
    }

    /**
     * 从JWT中提取所有声明
     *
     * @param: token
     * @return: io.jsonwebtoken.Claims
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-31 10:07
     * @since 1.0.0
     */
    private static Claims extractAllClaims(String token) {

        token = clearPrefix(token);

        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("JWT_SECRET_KEY environment variable is not set.");
        }

        return Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从JWT中提取用户名称
     *
     * @param: token
     * @return: java.lang.String
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-31 10:08
     * @since 1.0.0
     */
    public static String extractSubject(String token) {

        return extractAllClaims(token).getSubject();
    }

    /**
     * 检查令牌是否过期
     *
     * @param: token
     * @return: boolean
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-31 10:16
     * @since 1.0.0
     */
    public static boolean isTokenExpired(String token) {

        try {

            token = clearPrefix(token);
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception ex) {
            throw new RuntimeException("Invalid token, caused by:", ex);
        }
    }

    /**
     * 检查刷新令牌是否有效
     *
     * @param: refreshToken
     * @return: boolean
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-31 11:01
     * @since 1.0.0
     */
    public static boolean isRefreshTokenValid(String refreshToken) {

        refreshToken = clearPrefix(refreshToken);
        return BooleanUtil.isFalse(isTokenExpired(refreshToken));
    }

    /**
     * 使用刷新令牌生成新的访问令牌
     *
     * @param: refreshToken
     * @return: java.lang.String
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-31 11:02
     * @since 1.0.0
     */
    public static String refreshToken(String refreshToken) {

        refreshToken = clearPrefix(refreshToken);
        if (isRefreshTokenValid(refreshToken)) {
            Claims claims = extractAllClaims(refreshToken);
            String username = claims.getSubject();
            return generateToken(username);

        } else {
            throw new RuntimeException("Invalid refresh token: " + refreshToken);
        }
    }

    /**
     * 清理前缀
     *
     * @param: token
     * @return: java.lang.String
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2024-12-31 16:24
     * @since 1.0.0
     */
    public static String clearPrefix(String token) {
        return token.substring(7).trim();
    }

    public static void main(String[] args) {

        String token = JwtUtil.generateToken("test");
        System.out.println("Generated Token: " + token);

        Claims claims = JwtUtil.extractAllClaims(token);
        System.out.println(JSONObject.toJSONString(claims));

        String username = JwtUtil.extractSubject(token);
        System.out.println(username);
    }
}
