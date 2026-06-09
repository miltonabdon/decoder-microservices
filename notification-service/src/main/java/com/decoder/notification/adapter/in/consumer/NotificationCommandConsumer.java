package com.decoder.notification.adapter.in.consumer;

import com.decoder.notification.adapter.in.consumer.dto.NotificationCommandDto;
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
public class NotificationCommandConsumer {
    private final NotificationService notificationService;

    @RabbitListener(queues = "${decoder.rabbitmq.queues.notification-commands}")
    @Transactional
    public void handleCommand(NotificationCommandDto command) {
        log.info("NotificationCommand: type={} userId={}", command.commandType(), command.userId());
        notificationService.create(command.userId(), NotificationType.COMMAND_NOTIFICATION, command.title(), command.message());
    }
}
