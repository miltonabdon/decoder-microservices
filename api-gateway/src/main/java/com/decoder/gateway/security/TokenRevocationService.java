package com.decoder.gateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRevocationService {

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";
    private final StringRedisTemplate redisTemplate;

    public boolean isRevoked(String jti) {
        if (jti == null) return false;
        boolean revoked = Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + jti));
        if (revoked) log.warn("Rejected revoked token JTI: {}", jti);
        return revoked;
    }
}
