package com.decoder.notification.adapter.in.consumer.dto;
import java.time.LocalDateTime;
import java.util.UUID;
public record UserEventDto(UUID eventId, String eventType, UUID userId, String username, String email, String fullName, String userType, LocalDateTime timestamp) {}
