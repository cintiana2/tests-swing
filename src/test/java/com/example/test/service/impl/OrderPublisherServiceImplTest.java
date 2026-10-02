package com.example.test.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.MessageSource;

import com.example.test.config.LocaleConfig;
import com.example.test.config.RabbitMQConfig;
import com.example.test.vo.OrderRequestVO;
import com.example.test.vo.OrderStatusVO;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - OrderPublisherServiceImpl")
class OrderPublisherServiceImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RabbitMQConfig rabbitMQConfig;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private OrderPublisherServiceImpl orderPublisherService;

    @Test
    @DisplayName(" Deve obter a mensagem de log internacionalizada (i18n)")
    void deveObterMensagemI18nAOPublicarPedido() {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Monitor", 1, LocalDateTime.now());

        when(rabbitMQConfig.getExchangeName()).thenReturn("pedidos.exchange");
        when(rabbitMQConfig.getInputQueueName()).thenReturn("pedidos.entrada.seu-nome");
        when(messageSource.getMessage(eq("order.log.publishing"), any(), eq(LocaleConfig.PT_BR)))
                .thenReturn("Publicando pedido...");

        orderPublisherService.publishOrder(requestVO);

        verify(messageSource).getMessage(eq("order.log.publishing"), any(), eq(LocaleConfig.PT_BR));
    }

    @Test
    @DisplayName("Deve converter e enviar o payload para a exchange e fila")
    void deveEnviarPayloadParaExchangeEFilaDeEntrada() {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Monitor", 1, LocalDateTime.now());

        when(rabbitMQConfig.getExchangeName()).thenReturn("pedidos.exchange");
        when(rabbitMQConfig.getInputQueueName()).thenReturn("pedidos.entrada.seu-nome");

        orderPublisherService.publishOrder(requestVO);

        verify(rabbitTemplate).convertAndSend("pedidos.exchange", "pedidos.entrada.seu-nome", requestVO);
    }

    @Test
    @DisplayName("Deve enviar a mensagem para a fila de sucesso")
    void deveEnviarStatusSucessoParaFilaCorrespondente() {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Teclado", 2, LocalDateTime.now());
        when(rabbitMQConfig.getSuccessStatusQueueName()).thenReturn("pedidos.status.sucesso.seu-nome");

        orderPublisherService.publishSuccessStatus(requestVO);

        verify(rabbitTemplate).convertAndSend(eq("pedidos.status.sucesso.seu-nome"), any(OrderStatusVO.class));
    }

    @Test
    @DisplayName("Deve enviar a mensagem com a sua razão para a fila de erro")
    void deveEnviarStatusFalhaParaFilaCorrespondente() {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Mouse", 1, LocalDateTime.now());
        when(rabbitMQConfig.getFailureStatusQueueName()).thenReturn("pedidos.status.falha.seu-nome");

        orderPublisherService.publishFailureStatus(requestVO, "Erro simulado");

        verify(rabbitTemplate).convertAndSend(eq("pedidos.status.falha.seu-nome"), any(OrderStatusVO.class));
    }
}