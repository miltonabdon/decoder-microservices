package com.decoder.course.adapter.in.controller.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record ModuleRequestDto(@NotBlank String title, String description, @NotNull Integer sequenceNumber) {}
