package com.decoder.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("auth-user-route", r -> r
                .path("/auth/users/**", "/auth/users")
                .filters(f -> f
                    .circuitBreaker(c -> c
                        .setName("auth-user-cb")
                        .setFallbackUri("forward:/fallback/auth")))
                .uri("lb://auth-user-service"))

            .route("course-route", r -> r
                .path("/api/courses/**")
                .filters(f -> f
                    .circuitBreaker(c -> c
                        .setName("course-cb")
                        .setFallbackUri("forward:/fallback/courses")))
                .uri("lb://course-service"))

            .route("notification-route", r -> r
                .path("/api/notifications/**")
                .filters(f -> f
                    .circuitBreaker(c -> c
                        .setName("notification-cb")
                        .setFallbackUri("forward:/fallback/notifications")))
                .uri("lb://notification-service"))

            .build();
    }
}
