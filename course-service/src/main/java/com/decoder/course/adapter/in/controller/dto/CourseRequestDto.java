package com.decoder.course.adapter.in.controller.dto;
import com.decoder.course.domain.model.CourseLevel;
import com.decoder.course.domain.model.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CourseRequestDto(@NotBlank String name, String description, CourseLevel level, CourseStatus status, UUID instructorId) {}
