package com.example.test.controller;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.enums.OrderStatus;
import com.example.test.service.OrderService;
import com.example.test.vo.OrderRequestVO;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody OrderRequestVO orderRequest) {
        UUID orderId = orderService.createAndPublishOrder(orderRequest);
        Map<String, Object> response = Collections.singletonMap("orderId", orderId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<Map<String, Object>> getOrderStatus(@PathVariable("id") UUID orderId) {
        OrderStatus status = orderService.getOrderStatus(orderId);
        Map<String, Object> response = Collections.singletonMap("status", status.name());
        return ResponseEntity.ok(response);
    }
}