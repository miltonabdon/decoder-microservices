package com.decoder.notification.domain.service;

import com.decoder.notification.adapter.out.persistence.NotificationRepository;
import com.decoder.notification.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repository;

    @Transactional
    public NotificationModel create(UUID userId, NotificationType type, String title, String message) {
        var n = new NotificationModel();
        n.setUserId(userId); n.setType(type); n.setTitle(title); n.setMessage(message);
        n.setStatus(NotificationStatus.CREATED);
        var saved = repository.save(n);
        log.info("Notification created: id={} userId={} type={}", saved.getId(), userId, type);
        return saved;
    }

    public Page<NotificationModel> listByUser(UUID userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable);
    }

    public NotificationModel findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Notification not found: " + id));
    }

    @Transactional
    public NotificationModel markAsRead(UUID id) {
        var n = findById(id);
        n.setStatus(NotificationStatus.READ);
        n.setReadAt(LocalDateTime.now());
        return repository.save(n);
    }

    @Transactional
    public void delete(UUID id) { findById(id); repository.deleteById(id); }

    @Transactional
    public void deleteAllByUser(UUID userId) {
        repository.deleteAllByUserId(userId);
        log.info("All notifications deleted for userId={}", userId);
    }
}
