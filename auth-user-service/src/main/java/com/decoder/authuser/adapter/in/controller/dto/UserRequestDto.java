package com.decoder.authuser.adapter.in.controller.dto;

import com.decoder.authuser.domain.model.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
    @NotBlank @Size(min = 3, max = 50) String username,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6) String password,
    String fullName,
    UserType userType
) {}
