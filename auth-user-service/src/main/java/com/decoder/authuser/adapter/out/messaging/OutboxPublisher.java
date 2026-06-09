package com.decoder.authuser.adapter.out.messaging;

import com.decoder.authuser.adapter.out.persistence.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${decoder.rabbitmq.exchanges.users}")
    private String usersExchange;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        var events = outboxRepository.findUnpublished();
        if (events.isEmpty()) return;

        log.debug("Publishing {} outbox events", events.size());

        for (var event : events) {
            try {
                var routingKey = switch (event.getEventType()) {
                    case "USER_CREATED" -> "user.created";
                    case "USER_UPDATED" -> "user.updated";
                    case "USER_DELETED" -> "user.deleted";
                    default -> event.getEventType().toLowerCase().replace("_", ".");
                };

                var payload = objectMapper.readValue(event.getPayload(), Object.class);
                rabbitTemplate.convertAndSend(usersExchange, routingKey, payload);

                event.setPublishedAt(LocalDateTime.now());
                outboxRepository.save(event);
                log.info("Outbox event published: {} id={}", event.getEventType(), event.getId());

            } catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);
                outboxRepository.save(event);
                log.error("Failed to publish outbox event id={} attempt={}: {}",
                    event.getId(), event.getRetryCount(), e.getMessage());
            }
        }
    }
}
