package com.decoder.notification.adapter.in.consumer;

import com.decoder.notification.adapter.in.consumer.dto.EnrollmentEventDto;
import com.decoder.notification.domain.model.NotificationType;
import com.decoder.notification.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseEnrollmentConsumer {
    private final NotificationService notificationService;

    @RabbitListener(queues = "${decoder.rabbitmq.queues.enrollment-notification}")
    @Transactional
    public void handleEnrollmentEvent(EnrollmentEventDto event) {
        log.info("EnrollmentConsumer: type={} courseId={} userId={}", event.eventType(), event.courseId(), event.userId());
        if ("ENROLLMENT_CREATED".equals(event.eventType())) {
            notificationService.create(event.userId(), NotificationType.ENROLLMENT_CREATED,
                "Inscrição confirmada!", "Você se inscreveu no curso: " + event.courseName());
        } else if ("ENROLLMENT_DELETED".equals(event.eventType())) {
            notificationService.create(event.userId(), NotificationType.ENROLLMENT_DELETED,
                "Inscrição cancelada", "Sua inscrição no curso " + event.courseName() + " foi cancelada.");
        }
    }
}
