package com.example.test.service;

import java.util.UUID;
import com.example.test.enums.OrderStatus;
import com.example.test.vo.OrderRequestVO;

public interface OrderService {
    UUID createAndPublishOrder(OrderRequestVO orderRequest);
    OrderStatus getOrderStatus(UUID orderId);
}