package com.decoder.course.adapter.in.controller.dto;

import com.decoder.course.domain.model.CourseLevel;
import com.decoder.course.domain.model.CourseStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public interface CourseDetailView {
    UUID getId();
    String getName();
    String getDescription();
    CourseStatus getStatus();
    CourseLevel getLevel();
    UUID getInstructorId();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
