package com.example.test.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.test.enums.OrderStatus;
import com.example.test.service.OrderPublisherService;
import com.example.test.service.OrderStoreService;
import com.example.test.vo.OrderRequestVO;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - OrderServiceImpl")
class OrderServiceImplTest {

    @Mock
    private OrderPublisherService orderPublisherService;

    @Mock
    private OrderStoreService orderStoreService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Deve retornar o mesmo UUID do pedido recebido")
    void deveRetornarMesmoUUIDAoCriarPedido() {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Teclado", 1, LocalDateTime.now());

        UUID resultId = orderService.createAndPublishOrder(requestVO);

        assertEquals(orderId, resultId);
    }

    @Test
    @DisplayName("Deve atualizar o status inicial na Store para SENT_WAITING_PROCESS")
    void deveAtualizarStatusInicialNaStoreAoCriarPedido() {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Teclado", 1, LocalDateTime.now());

        orderService.createAndPublishOrder(requestVO);

        verify(orderStoreService).updateStatus(orderId, OrderStatus.SENT_WAITING_PROCESS);
    }

    @Test
    @DisplayName("Deve solicitar a publicação do pedido via OrderPublisherService")
    void deveSolicitarPublicacaoAOPublisherService() {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Teclado", 1, LocalDateTime.now());

        orderService.createAndPublishOrder(requestVO);

        verify(orderPublisherService).publishOrder(requestVO);
    }

    @Test
    @DisplayName("Deve consultar e retornar o status do pedido armazenado no OrderStoreService")
    void deveConsultarStatusNoStoreService() {
        UUID orderId = UUID.randomUUID();
        when(orderStoreService.getStatus(orderId)).thenReturn(OrderStatus.SUCCESS);

        OrderStatus status = orderService.getOrderStatus(orderId);

        assertEquals(OrderStatus.SUCCESS, status);
        verify(orderStoreService).getStatus(orderId);
    }
}