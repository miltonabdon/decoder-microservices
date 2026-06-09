package com.decoder.course.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_lessons")
public class LessonModel {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, length = 150) private String title;
    @Column(length = 500) private String description;
    private String videoUrl;
    @Column(nullable = false) private Integer sequenceNumber;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "module_id", nullable = false) private ModuleModel module;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @PrePersist public void prePersist() { createdAt = LocalDateTime.now(); }
}
