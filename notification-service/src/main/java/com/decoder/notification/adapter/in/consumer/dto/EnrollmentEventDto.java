package com.decoder.notification.adapter.in.consumer.dto;
import java.time.LocalDateTime;
import java.util.UUID;
public record EnrollmentEventDto(UUID eventId, String eventType, UUID courseId, String courseName, UUID userId, LocalDateTime timestamp) {}
