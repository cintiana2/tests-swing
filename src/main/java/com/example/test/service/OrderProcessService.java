package com.example.test.service;

import com.example.test.vo.OrderRequestVO;

public interface OrderProcessService {
    void processOrder(OrderRequestVO orderRequest);
}