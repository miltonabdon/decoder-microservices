package com.decoder.course.adapter.out.messaging;

import com.decoder.course.adapter.out.messaging.dto.EnrollmentEventDto;
import com.decoder.course.domain.model.CourseUserModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    @Value("${decoder.rabbitmq.exchanges.courses}") private String coursesExchange;
    @Value("${decoder.rabbitmq.routing-keys.enrollment-created}") private String enrollmentCreatedKey;
    @Value("${decoder.rabbitmq.routing-keys.enrollment-deleted}") private String enrollmentDeletedKey;

    public void publishEnrollmentCreated(CourseUserModel e) {
        log.info("Publishing ENROLLMENT_CREATED courseId={} userId={}", e.getCourse().getId(), e.getUserId());
        rabbitTemplate.convertAndSend(coursesExchange, enrollmentCreatedKey, EnrollmentEventDto.from("ENROLLMENT_CREATED", e));
    }

    public void publishEnrollmentDeleted(CourseUserModel e) {
        log.info("Publishing ENROLLMENT_DELETED courseId={} userId={}", e.getCourse().getId(), e.getUserId());
        rabbitTemplate.convertAndSend(coursesExchange, enrollmentDeletedKey, EnrollmentEventDto.from("ENROLLMENT_DELETED", e));
    }
}
