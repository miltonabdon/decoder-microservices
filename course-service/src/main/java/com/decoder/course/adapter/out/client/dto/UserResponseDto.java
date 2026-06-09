package com.decoder.course.adapter.out.client.dto;
import java.util.UUID;
public record UserResponseDto(UUID id, String username, String email, String fullName, String userType) {}
