package com.decoder.notification.adapter.in.consumer.dto;
import java.time.LocalDateTime;
import java.util.UUID;
public record NotificationCommandDto(UUID commandId, String commandType, UUID userId, String title, String message, LocalDateTime timestamp) {}
