package com.decoder.course.adapter.in.consumer;

import com.decoder.course.adapter.in.consumer.dto.UserEventDto;
import com.decoder.course.adapter.out.persistence.CourseUserRepository;
import com.decoder.course.adapter.out.persistence.UserDataRepository;
import com.decoder.course.domain.model.UserDataModel;
import com.decoder.course.domain.model.UserDataType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {
    private final UserDataRepository userDataRepository;
    private final CourseUserRepository courseUserRepository;

    @RabbitListener(queues = "${decoder.rabbitmq.queues.users-course}")
    @Transactional
    public void handleUserEvent(UserEventDto event) {
        log.info("UserEventConsumer: type={} userId={}", event.eventType(), event.userId());
        switch (event.eventType()) {
            case "USER_CREATED", "USER_UPDATED" -> syncUser(event);
            case "USER_DELETED" -> deleteUser(event);
            default -> log.warn("Unknown event: {}", event.eventType());
        }
    }

    private void syncUser(UserEventDto e) {
        var u = userDataRepository.findById(e.userId()).orElse(new UserDataModel());
        u.setId(e.userId());
        u.setUsername(e.username());
        u.setEmail(e.email());
        u.setFullName(e.fullName());
        u.setUserType(e.userType() != null ? e.userType() : UserDataType.STUDENT);
        userDataRepository.save(u);
    }

    private void deleteUser(UserEventDto e) {
        courseUserRepository.deleteAllByUserId(e.userId());
        userDataRepository.deleteById(e.userId());
        log.info("Deleted user data and enrollments for userId={}", e.userId());
    }
}
