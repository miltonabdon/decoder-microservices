package com.decoder.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    // Rate limiter para login: 5 req/min por IP
    @Bean
    public RedisRateLimiter loginRateLimiter() {
        return new RedisRateLimiter(5, 5, 60);
    }

    // Rate limiter geral: 100 req/min por IP
    @Bean
    public RedisRateLimiter defaultRateLimiter() {
        return new RedisRateLimiter(100, 100, 60);
    }

    // Resolve a chave pelo IP do cliente
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            var addr = exchange.getRequest().getRemoteAddress();
            String ip = addr != null ? addr.getAddress().getHostAddress() : "unknown";
            return Mono.just(ip);
        };
    }
}
