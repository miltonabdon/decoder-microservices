package com.decoder.course.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${decoder.rabbitmq.exchanges.users}") private String usersExchange;
    @Value("${decoder.rabbitmq.exchanges.courses}") private String coursesExchange;
    @Value("${decoder.rabbitmq.queues.users-course}") private String usersCourseQueue;
    @Value("${decoder.rabbitmq.queues.enrollment-notification}") private String enrollmentNotifQueue;

    @Bean public TopicExchange usersTopicExchange() { return new TopicExchange(usersExchange); }
    @Bean public TopicExchange coursesTopicExchange() { return new TopicExchange(coursesExchange); }
    @Bean public Queue usersCourseQueue() { return QueueBuilder.durable(usersCourseQueue).build(); }
    @Bean public Queue enrollmentNotifQueue() { return QueueBuilder.durable(enrollmentNotifQueue).build(); }

    @Bean
    public Binding usersCourseBinding(Queue usersCourseQueue, TopicExchange usersTopicExchange) {
        return BindingBuilder.bind(usersCourseQueue).to(usersTopicExchange).with("user.#");
    }

    @Bean
    public Binding enrollmentNotifBinding(Queue enrollmentNotifQueue, TopicExchange coursesTopicExchange) {
        return BindingBuilder.bind(enrollmentNotifQueue).to(coursesTopicExchange).with("enrollment.#");
    }

    @Bean public Jackson2JsonMessageConverter messageConverter() { return new Jackson2JsonMessageConverter(); }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        var t = new RabbitTemplate(cf);
        t.setMessageConverter(messageConverter());
        return t;
    }
}
