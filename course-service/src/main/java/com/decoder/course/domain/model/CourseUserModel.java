package com.decoder.course.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_courses_users", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "user_id"}))
public class CourseUserModel {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID userId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "course_id", nullable = false) private CourseModel course;
    @Column(nullable = false, updatable = false) private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    @Column(nullable = false) private int progress = 0;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private EnrollmentStatus status = EnrollmentStatus.ACTIVE;
    @PrePersist public void prePersist() { enrolledAt = LocalDateTime.now(); }
}
