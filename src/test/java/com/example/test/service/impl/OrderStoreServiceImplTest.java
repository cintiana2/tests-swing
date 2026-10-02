package com.example.test.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.test.enums.OrderStatus;

class OrderStoreServiceImplTest {

    private OrderStoreServiceImpl orderStoreService;

    @BeforeEach
    void setUp() {
        orderStoreService = new OrderStoreServiceImpl();
    }

    @Test
    @DisplayName("Deve retornar SENT_WAITING_PROCESS quando o pedido nao existir no mapa")
    void getStatus_DefaultStatus() {
        UUID orderId = UUID.randomUUID();
        OrderStatus status = orderStoreService.getStatus(orderId);

        assertEquals(OrderStatus.SENT_WAITING_PROCESS, status);
    }

    @Test
    @DisplayName("Deve armazenar e atualizar o status corretamente")
    void updateAndGetStatus_Success() {
        UUID orderId = UUID.randomUUID();

        orderStoreService.updateStatus(orderId, OrderStatus.PROCESSING);
        assertEquals(OrderStatus.PROCESSING, orderStoreService.getStatus(orderId));

        orderStoreService.updateStatus(orderId, OrderStatus.SUCCESS);
        assertEquals(OrderStatus.SUCCESS, orderStoreService.getStatus(orderId));
    }
}