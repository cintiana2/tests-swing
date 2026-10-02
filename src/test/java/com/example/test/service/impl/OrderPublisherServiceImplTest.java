package com.example.test.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
class OrderPublisherServiceImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RabbitMQConfig rabbitMQConfig;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private OrderPublisherServiceImpl orderPublisherService;

    @BeforeEach
    void setUp() {
        when(rabbitMQConfig.getExchangeName()).thenReturn("pedidos.exchange.cintia");
        when(rabbitMQConfig.getInputQueueName()).thenReturn("pedidos.entrada.cintia");
    }

    @Test
    @DisplayName("Deve obter mensagem i18n e enviar o payload para a exchange e fila configuradas")
    void publishOrder_Success() {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Monitor", 1, LocalDateTime.now());

        when(messageSource.getMessage(eq("order.log.publishing"), any(), eq(LocaleConfig.PT_BR)))
                .thenReturn("Publicando pedido...");

        orderPublisherService.publishOrder(requestVO);

        verify(messageSource).getMessage(eq("order.log.publishing"), any(), eq(LocaleConfig.PT_BR));
        verify(rabbitTemplate).convertAndSend("pedidos.exchange.cintia", "pedidos.entrada.cintia", requestVO);
    }
}