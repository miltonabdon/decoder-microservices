package com.decoder.authuser.adapter.in.security;

import com.decoder.authuser.domain.model.UserModel;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    private final SecretKey key;
    private final long expirationMs;

    public JwtProvider(
            @Value("${decoder.jwt.secret}") String secret,
            @Value("${decoder.jwt.expiration-ms:86400000}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserModel user) {
        var roles = user.getRoles().stream()
            .map(r -> r.getRoleType().name())
            .collect(Collectors.joining(","));
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("username", user.getUsername())
            .claim("roles", roles)
            .claim("userType", user.getUserType().name())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .id(UUID.randomUUID().toString())
            .signWith(key)
            .compact();
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    public String getRolesFromToken(String token) {
        return getClaims(token).get("roles", String.class);
    }

    public String getJtiFromToken(String token) {
        return getClaims(token).getId();
    }

    public long getRemainingMs(String token) {
        var exp = getClaims(token).getExpiration();
        long remaining = exp.getTime() - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    public boolean validateToken(String token) {
        try { getClaims(token); return true; }
        catch (JwtException | IllegalArgumentException e) { log.warn("Invalid JWT: {}", e.getMessage()); return false; }
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
