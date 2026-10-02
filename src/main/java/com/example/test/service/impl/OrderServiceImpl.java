package com.example.test.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.test.enums.OrderStatus;
import com.example.test.service.OrderPublisherService;
import com.example.test.service.OrderService;
import com.example.test.service.OrderStoreService;
import com.example.test.vo.OrderRequestVO;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderPublisherService orderPublisherService;
    private final OrderStoreService orderStoreService;

    public OrderServiceImpl(OrderPublisherService orderPublisherService, OrderStoreService orderStoreService) {
        this.orderPublisherService = orderPublisherService;
        this.orderStoreService = orderStoreService;
    }

    @Override
    public UUID createAndPublishOrder(OrderRequestVO orderRequest) {
        orderStoreService.updateStatus(orderRequest.getId(), OrderStatus.SENT_WAITING_PROCESS);
        orderPublisherService.publishOrder(orderRequest);
        return orderRequest.getId();
    }

    @Override
    public OrderStatus getOrderStatus(UUID orderId) {
        return orderStoreService.getStatus(orderId);
    }
}