package com.decoder.authuser.adapter.out.messaging;

import com.decoder.authuser.adapter.out.messaging.dto.UserEventDto;
import com.decoder.authuser.adapter.out.persistence.OutboxRepository;
import com.decoder.authuser.domain.model.OutboxEvent;
import com.decoder.authuser.domain.model.UserModel;
import com.decoder.authuser.domain.port.UserEventPublisherPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher implements UserEventPublisherPort {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public void publishUserCreated(UserModel user) {
        saveToOutbox("USER_CREATED", user);
    }

    @Override
    @SneakyThrows
    public void publishUserUpdated(UserModel user) {
        saveToOutbox("USER_UPDATED", user);
    }

    @Override
    @SneakyThrows
    public void publishUserDeleted(UserModel user) {
        saveToOutbox("USER_DELETED", user);
    }

    @SneakyThrows
    private void saveToOutbox(String eventType, UserModel user) {
        var dto = UserEventDto.from(eventType, user);
        var payload = objectMapper.writeValueAsString(dto);

        var event = new OutboxEvent();
        event.setAggregateType("User");
        event.setAggregateId(user.getId().toString());
        event.setEventType(eventType);
        event.setPayload(payload);

        outboxRepository.save(event);
        log.info("Outbox event saved: {} userId={}", eventType, user.getId());
    }
}
