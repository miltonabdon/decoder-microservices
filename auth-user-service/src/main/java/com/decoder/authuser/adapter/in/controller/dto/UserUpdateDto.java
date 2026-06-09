package com.decoder.authuser.adapter.in.controller.dto;
import jakarta.validation.constraints.Email;
public record UserUpdateDto(String fullName, @Email String email) {}
