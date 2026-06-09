package com.decoder.authuser.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

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

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        // Retry: 3 tentativas com backoff exponencial 1s→2s→4s
        var retryTemplate = new RetryTemplate();

        var backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(1000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(10000L);
        retryTemplate.setBackOffPolicy(backOff);

        var retryPolicy = new SimpleRetryPolicy(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
            .retryOperations(retryTemplate)
            .build());

        return factory;
    }
}
