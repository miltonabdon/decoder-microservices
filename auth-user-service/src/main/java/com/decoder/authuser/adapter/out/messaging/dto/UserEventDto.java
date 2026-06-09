package com.decoder.authuser.adapter.out.messaging.dto;

import com.decoder.authuser.domain.model.UserModel;
import com.decoder.authuser.domain.model.UserType;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserEventDto(
    UUID eventId, String eventType, UUID userId,
    String username, String email, String fullName,
    UserType userType, LocalDateTime timestamp
) {
    public static UserEventDto from(String eventType, UserModel user) {
        return new UserEventDto(
            UUID.randomUUID(), eventType, user.getId(),
            user.getUsername(), user.getEmail(), user.getFullName(),
            user.getUserType(), LocalDateTime.now()
        );
    }
}
