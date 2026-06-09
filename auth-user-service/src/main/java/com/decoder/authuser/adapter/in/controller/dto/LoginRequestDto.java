package com.decoder.authuser.adapter.in.controller.dto;
import jakarta.validation.constraints.NotBlank;
public record LoginRequestDto(@NotBlank String username, @NotBlank String password) {}
