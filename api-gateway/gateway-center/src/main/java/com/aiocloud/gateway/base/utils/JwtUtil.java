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
import java.util.*;

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

    private static List<String> audiencesCache;

    private static String audKey = "aud";

    static {
        audiencesCache = new ArrayList<>();
        generateRandomSecretKey();
    }

    /**
     * 添加 audience 到本地缓存中
     *
     * @param: audience
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-03 11:36
     * @since 1.0.0
     */
    public static void addAudienceCache(String audience) {

        if (BooleanUtil.isFalse(audiencesCache.contains(audience))) {
            audiencesCache.add(audience);
        }
    }

    /**
     * 创建 token
     *
     * @param: username
     * @param: issuer
     * @param: audience
     * @return: java.lang.String
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-03 11:22
     * @since 1.0.0
     */
    public static String generateToken(String username, String issuer, String audience) {

        List<String> audiences = new ArrayList<>();
        audiences.add(audience);

        return generateToken(username, issuer, audiences);
    }

    /**
     * 创建 token
     *
     * @param: username
     * @param: issuer
     * @param: audiences
     * @return: java.lang.String
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-03 11:23
     * @since 1.0.0
     */
    public static String generateToken(String username, String issuer, List<String> audiences) {

        Map<String, Object> claims = new HashMap<>();
        claims.put(audKey, audiences);

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .setClaims(claims)
                .compact();
    }

    /**
     * 创建刷新 token
     *
     * @param: username
     * @param: issuer
     * @param: audience
     * @return: java.lang.String
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-03 11:25
     * @since 1.0.0
     */
    public static String generateRefreshToken(String username, String issuer, String audience) {

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    /**
     * 创建 Random Secret Key
     *
     * @return: void
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-03 11:25
     * @since 1.0.0
     */
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
     * 验签
     *
     * @param: token
     * @return: java.lang.Boolean
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-02 20:43
     * @since 1.0.0
     */
    public static Boolean verifySignature(String token) {

        Claims claims = extractAllClaims(token);

        return Objects.isNull(claims);
    }

    public static boolean validateIssuer(String token, String issuer) {

        try {

            Claims claims = extractAllClaims(token);
            return issuer.equals(claims.getIssuer());

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 校验 audience
     *
     * @param: token
     * @param: audience
     * @return: boolean
     * @author: panyong
     * @version: 1.0.0
     * @createTime: 2025-01-03 11:26
     * @since 1.0.0
     */
    public static boolean validateAudience(String token) {

        try {

            Claims claims = extractAllClaims(token);
            Object audClaim = claims.get(audKey);
            if (audClaim instanceof String) {
                return audiencesCache.contains(audClaim);
            } else if (audClaim instanceof List) {
                List<?> audList = (List<?>) audClaim;
                return audiencesCache.containsAll(audList);
            }

            return false;

        } catch (Exception e) {
            return false;
        }
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
    public static String refreshToken(String refreshToken, String issuer, String audience) {

        refreshToken = clearPrefix(refreshToken);
        if (isRefreshTokenValid(refreshToken)) {
            Claims claims = extractAllClaims(refreshToken);
            String username = claims.getSubject();
            return generateToken(username, issuer, audience);

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

        String token = JwtUtil.generateToken("test", "test", "test");
        System.out.println("Generated Token: " + token);

        Claims claims = JwtUtil.extractAllClaims(token);
        System.out.println(JSONObject.toJSONString(claims));

        String username = JwtUtil.extractSubject(token);
        System.out.println(username);

        boolean validateAudience = JwtUtil.validateAudience(token);
        System.out.println(validateAudience);
    }

}
