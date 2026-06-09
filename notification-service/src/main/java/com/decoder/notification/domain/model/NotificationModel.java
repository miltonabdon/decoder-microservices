package com.decoder.notification.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_notifications")
public class NotificationModel {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID userId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private NotificationType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private NotificationStatus status = NotificationStatus.CREATED;
    @Column(nullable = false, length = 200) private String title;
    @Column(nullable = false, length = 1000) private String message;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    private LocalDateTime readAt;
    @PrePersist public void prePersist() { createdAt = LocalDateTime.now(); }
}
