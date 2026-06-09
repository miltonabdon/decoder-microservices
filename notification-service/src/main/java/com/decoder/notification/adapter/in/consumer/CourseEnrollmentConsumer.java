package com.decoder.notification.adapter.in.consumer;

import com.decoder.notification.adapter.in.consumer.dto.EnrollmentEventDto;
import com.decoder.notification.adapter.out.persistence.ProcessedEventRepository;
import com.decoder.notification.adapter.out.persistence.ProcessedEvent;
import com.decoder.notification.domain.model.NotificationType;
import com.decoder.notification.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseEnrollmentConsumer {
    private final NotificationService notificationService;
    private final ProcessedEventRepository processedEventRepository;

    @RabbitListener(queues = "${decoder.rabbitmq.queues.enrollment-notification}")
    @Transactional
    public void handleEnrollmentEvent(EnrollmentEventDto event) {
        if (processedEventRepository.existsById(event.eventId())) {
            log.warn("Duplicate event skipped: id={} type={}", event.eventId(), event.eventType());
            return;
        }

        log.info("EnrollmentConsumer: type={} courseId={} userId={}", event.eventType(), event.courseId(), event.userId());
        if ("ENROLLMENT_CREATED".equals(event.eventType())) {
            notificationService.create(event.userId(), NotificationType.ENROLLMENT_CREATED,
                "Inscrição confirmada!", "Você se inscreveu no curso: " + event.courseName());
        } else if ("ENROLLMENT_DELETED".equals(event.eventType())) {
            notificationService.create(event.userId(), NotificationType.ENROLLMENT_DELETED,
                "Inscrição cancelada", "Sua inscrição no curso " + event.courseName() + " foi cancelada.");
        }

        processedEventRepository.save(new ProcessedEvent(event.eventId(), event.eventType(), LocalDateTime.now()));
    }
}
