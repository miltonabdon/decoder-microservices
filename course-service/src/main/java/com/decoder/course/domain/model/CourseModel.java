package com.decoder.course.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_courses")
public class CourseModel {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, length = 150) private String name;
    @Column(length = 500) private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private CourseStatus status = CourseStatus.INPROGRESS;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private CourseLevel level = CourseLevel.BEGINNER;
    private UUID instructorId;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;
    @PrePersist public void prePersist() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate public void preUpdate() { updatedAt = LocalDateTime.now(); }
}
