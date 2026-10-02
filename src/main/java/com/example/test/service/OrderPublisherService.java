package com.example.test.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.example.test.config.LocaleConfig;
import com.example.test.config.RabbitMQConfig;
import com.example.test.vo.OrderRequestVO;

@Service
public class OrderPublisherService {

    private static final Logger log = LoggerFactory.getLogger(OrderPublisherService.class);

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;
    private final MessageSource messageSource;

    public OrderPublisherService(RabbitTemplate rabbitTemplate, RabbitMQConfig rabbitMQConfig, MessageSource messageSource) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQConfig = rabbitMQConfig;
        this.messageSource = messageSource;
    }

    public void publishOrder(OrderRequestVO orderRequest) {
        String logMsg = messageSource.getMessage("order.log.publishing", new Object[]{orderRequest.getId()}, LocaleConfig.PT_BR);
        log.info(logMsg);

        rabbitTemplate.convertAndSend(
                rabbitMQConfig.getExchangeName(),
                rabbitMQConfig.getInputQueueName(),
                orderRequest
        );
    }
}