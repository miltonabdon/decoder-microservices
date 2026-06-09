package com.decoder.authuser.adapter.out.messaging;

import com.decoder.authuser.adapter.out.messaging.dto.UserEventDto;
import com.decoder.authuser.domain.model.UserModel;
import com.decoder.authuser.domain.port.UserEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher implements UserEventPublisherPort {
    private final RabbitTemplate rabbitTemplate;

    @Value("${decoder.rabbitmq.exchanges.users}") private String usersExchange;
    @Value("${decoder.rabbitmq.routing-keys.user-created}") private String userCreatedKey;
    @Value("${decoder.rabbitmq.routing-keys.user-updated}") private String userUpdatedKey;
    @Value("${decoder.rabbitmq.routing-keys.user-deleted}") private String userDeletedKey;

    @Override
    public void publishUserCreated(UserModel user) {
        log.info("Publishing USER_CREATED userId={}", user.getId());
        rabbitTemplate.convertAndSend(usersExchange, userCreatedKey, UserEventDto.from("USER_CREATED", user));
    }

    @Override
    public void publishUserUpdated(UserModel user) {
        log.info("Publishing USER_UPDATED userId={}", user.getId());
        rabbitTemplate.convertAndSend(usersExchange, userUpdatedKey, UserEventDto.from("USER_UPDATED", user));
    }

    @Override
    public void publishUserDeleted(UserModel user) {
        log.info("Publishing USER_DELETED userId={}", user.getId());
        rabbitTemplate.convertAndSend(usersExchange, userDeletedKey, UserEventDto.from("USER_DELETED", user));
    }
}
