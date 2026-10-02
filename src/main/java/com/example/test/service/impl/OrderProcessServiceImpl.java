package com.example.test.service.impl;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.example.test.config.LocaleConfig;
import com.example.test.enums.OrderStatus;
import com.example.test.exception.ProcessingException;
import com.example.test.service.OrderProcessService;
import com.example.test.service.OrderPublisherService;
import com.example.test.service.OrderStoreService;
import com.example.test.vo.OrderRequestVO;

@Service
public class OrderProcessServiceImpl implements OrderProcessService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessServiceImpl.class);
    private final Random random = new Random();

    private final OrderStoreService orderStoreService;
    private final OrderPublisherService orderPublisherService;
    private final MessageSource messageSource;

    public OrderProcessServiceImpl(OrderStoreService orderStoreService,
                                  OrderPublisherService orderPublisherService,
                                  MessageSource messageSource) {
        this.orderStoreService = orderStoreService;
        this.orderPublisherService = orderPublisherService;
        this.messageSource = messageSource;
    }

    @Override
    public void processOrder(OrderRequestVO orderRequest) {
        String msgStarted = messageSource.getMessage("order.log.started", new Object[]{orderRequest.getId()}, LocaleConfig.PT_BR);
        log.info(msgStarted);

        orderStoreService.updateStatus(orderRequest.getId(), OrderStatus.PROCESSING);

        try {
            int delaySeconds = 1000 + random.nextInt(2000);
            Thread.sleep(delaySeconds);

            if (random.nextDouble() < 0.20) {
                String simExcMsg = messageSource.getMessage("order.exception.simulated", null, LocaleConfig.PT_BR);
                throw new ProcessingException(simExcMsg);
            }

            String msgSuccess = messageSource.getMessage("order.log.success", new Object[]{orderRequest.getId()}, LocaleConfig.PT_BR);
            log.info(msgSuccess);

            orderStoreService.updateStatus(orderRequest.getId(), OrderStatus.SUCCESS);
            orderPublisherService.publishSuccessStatus(orderRequest);

        } catch (ProcessingException | InterruptedException e) {
            String msgFailed = messageSource.getMessage("order.log.failed", new Object[]{orderRequest.getId(), e.getMessage()}, LocaleConfig.PT_BR);
            log.error(msgFailed);

            orderStoreService.updateStatus(orderRequest.getId(), OrderStatus.FAILURE);
            orderPublisherService.publishFailureStatus(orderRequest, e.getMessage());

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            String procExcMsg = messageSource.getMessage("order.exception.processing", new Object[]{orderRequest.getId()}, LocaleConfig.PT_BR);
            throw new ProcessingException(procExcMsg);
        }
    }
}