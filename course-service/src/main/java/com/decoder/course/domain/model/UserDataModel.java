package com.decoder.course.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_users_data")
public class UserDataModel {
    @Id private UUID id;
    @Column(unique = true, nullable = false, length = 50) private String username;
    @Column(unique = true, nullable = false, length = 100) private String email;
    @Column(length = 150) private String fullName;
    @Enumerated(EnumType.STRING) private UserDataType userType;
    @Column(nullable = false) private LocalDateTime syncedAt;
    @PrePersist @PreUpdate public void preOp() { syncedAt = LocalDateTime.now(); }
}
