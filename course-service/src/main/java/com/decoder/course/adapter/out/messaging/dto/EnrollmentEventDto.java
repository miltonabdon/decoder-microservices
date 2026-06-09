package com.decoder.course.adapter.out.messaging.dto;
import com.decoder.course.domain.model.CourseUserModel;
import java.time.LocalDateTime;
import java.util.UUID;
public record EnrollmentEventDto(UUID eventId, String eventType, UUID courseId, String courseName, UUID userId, LocalDateTime timestamp) {
    public static EnrollmentEventDto from(String type, CourseUserModel e) {
        return new EnrollmentEventDto(UUID.randomUUID(), type, e.getCourse().getId(), e.getCourse().getName(), e.getUserId(), LocalDateTime.now());
    }
}
