package com.example.test.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.test.enums.OrderStatus;

@Service
public class OrderStoreService {

    private final Map<UUID, OrderStatus> orderStatusStore = new ConcurrentHashMap<>();

    public void updateStatus(UUID orderId, OrderStatus status) {
        orderStatusStore.put(orderId, status);
    }

    public OrderStatus getStatus(UUID orderId) {
        return orderStatusStore.getOrDefault(orderId, OrderStatus.SENT_WAITING_PROCESS);
    }
}