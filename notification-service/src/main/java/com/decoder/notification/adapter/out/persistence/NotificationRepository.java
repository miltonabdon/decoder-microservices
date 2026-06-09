package com.decoder.notification.adapter.out.persistence;

import com.decoder.notification.domain.model.NotificationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationModel, UUID> {
    Page<NotificationModel> findByUserId(UUID userId, Pageable pageable);
    void deleteAllByUserId(UUID userId);
}
