package com.example.test.service;

import com.example.test.vo.OrderRequestVO;

public interface OrderPublisherService {
    void publishOrder(OrderRequestVO orderRequest);
    void publishSuccessStatus(OrderRequestVO orderRequest);
    void publishFailureStatus(OrderRequestVO orderRequest, String errorMessage);
}