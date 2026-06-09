package com.decoder.authuser.config;

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
    @Value("${decoder.rabbitmq.queues.users-course}") private String usersCourseQueue;
    @Value("${decoder.rabbitmq.queues.users-notification}") private String usersNotifQueue;

    @Bean public TopicExchange usersTopicExchange() { return new TopicExchange(usersExchange); }
    @Bean public Queue usersCourseQueue() { return QueueBuilder.durable(usersCourseQueue).build(); }
    @Bean public Queue usersNotifQueue() { return QueueBuilder.durable(usersNotifQueue).build(); }

    @Bean
    public Binding usersCourseBinding() {
        return BindingBuilder.bind(usersCourseQueue()).to(usersTopicExchange()).with("user.#");
    }

    @Bean
    public Binding usersNotifBinding() {
        return BindingBuilder.bind(usersNotifQueue()).to(usersTopicExchange()).with("user.#");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() { return new Jackson2JsonMessageConverter(); }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        var t = new RabbitTemplate(cf);
        t.setMessageConverter(messageConverter());
        return t;
    }
}
