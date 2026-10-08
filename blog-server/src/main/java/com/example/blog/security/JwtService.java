package com.example.blog.security;

import com.example.blog.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationSeconds;

    public JwtService(@Value("${blog.jwt.secret}") String secret,
                      @Value("${blog.jwt.expiration-hours}") long expirationHours) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationHours * 3600;
    }

    public String create(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .claim("tokenVersion", user.getTokenVersion() == null ? 0 : user.getTokenVersion())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key)
                .compact();
    }

    /** 校验签名和时效后返回认证声明；解析失败会抛出 JWT 异常。 */
    public JwtPayload parse(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return new JwtPayload(Long.valueOf(claims.getSubject()), claims.get("username", String.class),
                claims.get("role", String.class), claims.get("tokenVersion", Integer.class));
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}
