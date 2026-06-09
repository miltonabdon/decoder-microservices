package com.decoder.course.adapter.in.controller.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record LessonRequestDto(@NotBlank String title, String description, String videoUrl, @NotNull Integer sequenceNumber) {}
