package com.example.test.service.impl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.test.enums.OrderStatus;
import com.example.test.service.OrderStoreService;

@Service
public class OrderStoreServiceImpl implements OrderStoreService {

    private final Map<UUID, OrderStatus> orderStatusStore = new ConcurrentHashMap<>();

    @Override
    public void updateStatus(UUID orderId, OrderStatus status) {
        orderStatusStore.put(orderId, status);
    }

    @Override
    public OrderStatus getStatus(UUID orderId) {
        return orderStatusStore.getOrDefault(orderId, OrderStatus.SENT_WAITING_PROCESS);
    }
}