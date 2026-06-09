package com.decoder.notification.adapter.in.consumer;

import com.decoder.notification.adapter.in.consumer.dto.UserEventDto;
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
public class UserEventConsumer {
    private final NotificationService notificationService;

    @RabbitListener(queues = "${decoder.rabbitmq.queues.users-notification}")
    @Transactional
    public void handleUserEvent(UserEventDto event) {
        log.info("UserEventConsumer: type={} userId={}", event.eventType(), event.userId());
        switch (event.eventType()) {
            case "USER_CREATED" -> notificationService.create(event.userId(), NotificationType.USER_CREATED,
                "Bem-vindo à Decoder!", "Olá " + event.fullName() + ", sua conta foi criada com sucesso.");
            case "USER_DELETED" -> notificationService.deleteAllByUser(event.userId());
            default -> log.warn("Unhandled user event: {}", event.eventType());
        }
    }
}
