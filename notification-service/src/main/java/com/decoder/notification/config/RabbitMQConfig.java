package com.decoder.notification.config;

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
    @Value("${decoder.rabbitmq.exchanges.enrollments}") private String enrollmentsExchange;
    @Value("${decoder.rabbitmq.exchanges.notification-commands}") private String commandsExchange;
    @Value("${decoder.rabbitmq.queues.users-notification}") private String usersNotifQueue;
    @Value("${decoder.rabbitmq.queues.enrollment-notification}") private String enrollmentNotifQueue;
    @Value("${decoder.rabbitmq.queues.notification-commands}") private String commandsQueue;
    @Value("${decoder.rabbitmq.queues.notification-commands-dlq}") private String commandsDlq;

    @Bean public TopicExchange usersTopicExchange() { return new TopicExchange(usersExchange); }
    @Bean public TopicExchange enrollmentsTopicExchange() { return new TopicExchange(enrollmentsExchange); }
    @Bean public TopicExchange commandsTopicExchange() { return new TopicExchange(commandsExchange); }
    @Bean public Queue usersNotifQueue() { return QueueBuilder.durable(usersNotifQueue).build(); }
    @Bean public Queue enrollmentNotifQueue() { return QueueBuilder.durable(enrollmentNotifQueue).build(); }

    @Bean
    public Queue commandsQueue() {
        return QueueBuilder.durable(commandsQueue)
            .withArgument("x-dead-letter-exchange", "")
            .withArgument("x-dead-letter-routing-key", commandsDlq)
            .build();
    }

    @Bean public Queue commandsDlqQueue() { return QueueBuilder.durable(commandsDlq).build(); }

    @Bean
    public Binding usersNotifBinding() {
        return BindingBuilder.bind(usersNotifQueue()).to(usersTopicExchange()).with("user.#");
    }

    @Bean
    public Binding enrollmentNotifBinding() {
        return BindingBuilder.bind(enrollmentNotifQueue()).to(enrollmentsTopicExchange()).with("enrollment.#");
    }

    @Bean
    public Binding commandsBinding() {
        return BindingBuilder.bind(commandsQueue()).to(commandsTopicExchange()).with("notification.#");
    }

    @Bean public Jackson2JsonMessageConverter messageConverter() { return new Jackson2JsonMessageConverter(); }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        var t = new RabbitTemplate(cf);
        t.setMessageConverter(messageConverter());
        return t;
    }
}
