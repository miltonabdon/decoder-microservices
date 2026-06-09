package com.decoder.gateway.filter;

import com.decoder.gateway.security.JwtUtil;
import com.decoder.gateway.security.TokenRevocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final TokenRevocationService tokenRevocationService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();
        var path = request.getPath().value();
        var method = request.getMethod().name();

        // Rotas publicas por metodo+path (sem lista genérica para evitar bypass por prefixo)
        boolean isPublic =
            // POST /auth/users — registro de novos usuários
            (path.equals("/auth/users") && "POST".equals(method)) ||
            // POST /auth/users/login — autenticação
            (path.equals("/auth/users/login") && "POST".equals(method)) ||
            // POST /auth/users/logout — revogação de token (token validado internamente)
            (path.equals("/auth/users/logout") && "POST".equals(method)) ||
            // Actuator (health checks)
            path.startsWith("/actuator") ||
            // Fallback endpoints do Circuit Breaker
            path.startsWith("/fallback");

        if (isPublic) {
            return chain.filter(exchange);
        }

        // Extrair Bearer token
        var authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        var token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            log.warn("Invalid JWT token for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Checar blacklist
        var jti = jwtUtil.getJtiFromToken(token);
        if (tokenRevocationService.isRevoked(jti)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Injetar headers para os serviços downstream
        var userId = jwtUtil.getUserId(token);
        var roles = jwtUtil.getRoles(token);
        var username = jwtUtil.getUsername(token);

        var mutatedRequest = request.mutate()
            .header("X-User-Id", userId.toString())
            .header("X-User-Roles", roles != null ? roles : "")
            .header("X-Username", username != null ? username : "")
            .build();

        log.debug("JWT validated for userId={} path={}", userId, path);
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -1; // Executa antes de outros filtros
    }
}
