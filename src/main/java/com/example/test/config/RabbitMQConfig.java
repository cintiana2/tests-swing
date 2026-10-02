package com.example.test.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.custom-suffix:cintia}")
    private String customSuffix;

    public String getInputQueueName() {
        return "orders.input." + customSuffix;
    }

    public String getDlqQueueName() {
        return "orders.input." + customSuffix + ".dlq";
    }

    public String getSuccessStatusQueueName() {
        return "orders.status.success." + customSuffix;
    }

    public String getFailureStatusQueueName() {
        return "orders.status.failure." + customSuffix;
    }

    public String getExchangeName() {
        return "orders.exchange." + customSuffix;
    }

    public String getDlqExchangeName() {
        return "orders.dlq.exchange." + customSuffix;
    }

    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(getExchangeName());
    }

    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(getDlqExchangeName());
    }

    @Bean
    public Queue inputQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", getDlqExchangeName());
        args.put("x-dead-letter-routing-key", getDlqQueueName());
        return QueueBuilder.durable(getInputQueueName()).withArguments(args).build();
    }

    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(getDlqQueueName()).build();
    }

    @Bean
    public Queue successStatusQueue() {
        return QueueBuilder.durable(getSuccessStatusQueueName()).build();
    }

    @Bean
    public Queue failureStatusQueue() {
        return QueueBuilder.durable(getFailureStatusQueueName()).build();
    }

    @Bean
    public Binding inputBinding() {
        return BindingBuilder.bind(inputQueue()).to(mainExchange()).with(getInputQueueName());
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue()).to(dlqExchange()).with(getDlqQueueName());
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}