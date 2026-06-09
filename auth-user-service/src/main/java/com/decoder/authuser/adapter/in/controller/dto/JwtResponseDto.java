package com.decoder.authuser.adapter.in.controller.dto;
import java.util.UUID;
public record JwtResponseDto(String token, String type, UUID userId, String username, String email) {
    public JwtResponseDto(String token, UUID userId, String username, String email) {
        this(token, "Bearer", userId, username, email);
    }
}
