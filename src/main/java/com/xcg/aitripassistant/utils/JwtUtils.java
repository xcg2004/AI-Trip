package com.xcg.aitripassistant.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtils {

    // 自动生成符合 HS256 要求的密钥（至少 256 bit）
    private static final String SECRET_KEY = "javax.crypto.spec.SecretKeySpec@fa77d7c5";

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    // 过期时间（例如 15 分钟）
    private static final long EXPIRATION = 900000; // 毫秒

    public static String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY) // 使用 SecretKey 而不是 String
                .compact();
    }

    public static String parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static void main(String[] args) {
        System.out.println(Keys.secretKeyFor(SignatureAlgorithm.HS256));
    }
}
