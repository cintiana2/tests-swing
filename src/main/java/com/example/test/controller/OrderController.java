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
import com.example.test.service.OrderPublisherService;
import com.example.test.service.OrderStoreService;
import com.example.test.vo.OrderRequestVO;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderPublisherService orderPublisherService;
    private final OrderStoreService orderStoreService;

    public OrderController(OrderPublisherService orderPublisherService, OrderStoreService orderStoreService) {
        this.orderPublisherService = orderPublisherService;
        this.orderStoreService = orderStoreService;
    }

    /**
     * Endpoint para criação e envio assíncrono do pedido.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody OrderRequestVO orderRequest) {
        orderStoreService.updateStatus(orderRequest.getId(), OrderStatus.SENT_WAITING_PROCESS);
        orderPublisherService.publishOrder(orderRequest);

        Map<String, Object> response = Collections.singletonMap("orderId", orderRequest.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Endpoint para consulta de status do pedido via polling.
     */
    @GetMapping("/status/{id}")
    public ResponseEntity<Map<String, Object>> getOrderStatus(@PathVariable("id") UUID orderId) {
        OrderStatus status = orderStoreService.getStatus(orderId);
        Map<String, Object> response = Collections.singletonMap("status", status.name());
        return ResponseEntity.ok(response);
    }
}
