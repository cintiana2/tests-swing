package com.example.test.service;

import java.util.UUID;
import com.example.test.enums.OrderStatus;

public interface OrderStoreService {
    void updateStatus(UUID orderId, OrderStatus status);
    OrderStatus getStatus(UUID orderId);
}