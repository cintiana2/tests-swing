package com.example.test.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.example.test.config.LocaleConfig;
import com.example.test.config.RabbitMQConfig;
import com.example.test.service.OrderPublisherService;
import com.example.test.vo.OrderRequestVO;
import com.example.test.vo.OrderStatusVO;

@Service
public class OrderPublisherServiceImpl implements OrderPublisherService {

    private static final Logger log = LoggerFactory.getLogger(OrderPublisherServiceImpl.class);

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;
    private final MessageSource messageSource;

    public OrderPublisherServiceImpl(RabbitTemplate rabbitTemplate, 
                                     RabbitMQConfig rabbitMQConfig, 
                                     MessageSource messageSource) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQConfig = rabbitMQConfig;
        this.messageSource = messageSource;
    }

    @Override
    public void publishOrder(OrderRequestVO orderRequest) {
        String logMsg = messageSource.getMessage("order.log.publishing", new Object[]{orderRequest.getId()}, LocaleConfig.PT_BR);
        log.info(logMsg);

        rabbitTemplate.convertAndSend(
                rabbitMQConfig.getExchangeName(),
                rabbitMQConfig.getInputQueueName(),
                orderRequest
        );
    }

    @Override
    public void publishSuccessStatus(OrderRequestVO orderRequest) {
        OrderStatusVO successStatus = OrderStatusVO.success(orderRequest.getId());
        rabbitTemplate.convertAndSend(rabbitMQConfig.getSuccessStatusQueueName(), successStatus);
    }

    @Override
    public void publishFailureStatus(OrderRequestVO orderRequest, String errorMessage) {
        OrderStatusVO failureStatus = OrderStatusVO.failure(orderRequest.getId(), errorMessage);
        rabbitTemplate.convertAndSend(rabbitMQConfig.getFailureStatusQueueName(), failureStatus);
    }
}