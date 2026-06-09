package com.decoder.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, String>> authFallback() {
        log.warn("Circuit breaker opened for auth-user-service");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
            "status", "SERVICE_UNAVAILABLE",
            "service", "auth-user-service",
            "message", "Auth service is temporarily unavailable. Please try again later."
        ));
    }

    @GetMapping("/courses")
    public ResponseEntity<Map<String, String>> courseFallback() {
        log.warn("Circuit breaker opened for course-service");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
            "status", "SERVICE_UNAVAILABLE",
            "service", "course-service",
            "message", "Course service is temporarily unavailable. Please try again later."
        ));
    }

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, String>> notificationFallback() {
        log.warn("Circuit breaker opened for notification-service");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
            "status", "SERVICE_UNAVAILABLE",
            "service", "notification-service",
            "message", "Notification service is temporarily unavailable. Please try again later."
        ));
    }

    @GetMapping("/rate-limit")
    public ResponseEntity<Map<String, String>> rateLimitFallback() {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
            "status", "TOO_MANY_REQUESTS",
            "message", "Rate limit exceeded. Please retry after 60 seconds.",
            "retryAfter", "60"
        ));
    }
}
