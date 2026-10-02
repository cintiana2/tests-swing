package com.example.test.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.test.enums.OrderStatus;

@DisplayName("Testes Unitários - OrderStoreServiceImpl")
class OrderStoreServiceImplTest {

    private OrderStoreServiceImpl orderStoreService;

    @BeforeEach
    void setUp() {
        orderStoreService = new OrderStoreServiceImpl();
    }

    @Test
    @DisplayName("Deve retornar SENT_WAITING_PROCESS quando o ID não existe na Store")
    void deveRetornarSentWaitingProcessQuandoPedidoNaoExiste() {
        UUID orderId = UUID.randomUUID();

        OrderStatus status = orderStoreService.getStatus(orderId);

        assertEquals(OrderStatus.SENT_WAITING_PROCESS, status);
    }

    @Test
    @DisplayName("Deve armazenar o status PROCESSING correto")
    void deveArmazenarEBuscarStatusProcessing() {
        UUID orderId = UUID.randomUUID();

        orderStoreService.updateStatus(orderId, OrderStatus.PROCESSING);

        assertEquals(OrderStatus.PROCESSING, orderStoreService.getStatus(orderId));
    }

    @Test
    @DisplayName("Deve atualizar o estado para SUCCESS após novo update")
    void deveAtualizarParaStatusSuccess() {
        UUID orderId = UUID.randomUUID();

        orderStoreService.updateStatus(orderId, OrderStatus.PROCESSING);
        orderStoreService.updateStatus(orderId, OrderStatus.SUCCESS);

        assertEquals(OrderStatus.SUCCESS, orderStoreService.getStatus(orderId));
    }

    @Test
    @DisplayName(" Deve atualizar o estado para FAILURE quando falha")
    void deveAtualizarParaStatusFailure() {
        UUID orderId = UUID.randomUUID();

        orderStoreService.updateStatus(orderId, OrderStatus.PROCESSING);
        orderStoreService.updateStatus(orderId, OrderStatus.FAILURE);

        assertEquals(OrderStatus.FAILURE, orderStoreService.getStatus(orderId));
    }
}