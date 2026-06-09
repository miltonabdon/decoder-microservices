package com.decoder.course.adapter.in.consumer.dto;
import com.decoder.course.domain.model.UserDataType;
import java.time.LocalDateTime;
import java.util.UUID;
public record UserEventDto(UUID eventId, String eventType, UUID userId, String username, String email, String fullName, UserDataType userType, LocalDateTime timestamp) {}
