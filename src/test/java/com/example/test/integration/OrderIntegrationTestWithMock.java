package com.example.test.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.test.config.RabbitMQConfig;
import com.example.test.enums.OrderStatus;
import com.example.test.service.OrderStoreService;
import com.example.test.vo.OrderRequestVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.rabbitmq.listener.simple.auto-startup=false"
})
@DisplayName("Testes de Integração REST e Serviços (Mock RabbitMQ)")
class OrderIntegrationTestWithMock {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderStoreService orderStoreService;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("POST /api/orders - Deve retornar 202 e orderId")
    void deveRetornarHttp202AoCriarPedido() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Notebook Gamer", 1, LocalDateTime.now());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestVO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()));
    }

    @Test
    @DisplayName("Deve enviar mensagem para a Exchange e Fila configuradas")
    void devePublicarMensagemNaFilaRabbitMQ() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Teclado Mecanico", 2, LocalDateTime.now());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestVO)));

        verify(rabbitTemplate).convertAndSend(
                eq(rabbitMQConfig.getExchangeName()),
                eq(rabbitMQConfig.getInputQueueName()),
                any(OrderRequestVO.class)
        );
    }

    @Test
    @DisplayName(" Deve gravar o status inicial SENT_WAITING_PROCESS")
    void deveGravarStatusInicialNaStore() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Monitor 144Hz", 1, LocalDateTime.now());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestVO)));

        OrderStatus statusEmMemoria = orderStoreService.getStatus(orderId);
        assertThat(statusEmMemoria).isEqualTo(OrderStatus.SENT_WAITING_PROCESS);
    }

    @Test
    @DisplayName("Deve retornar o status atual do pedido via HTTP")
    void deveConsultarStatusViaEndpointGet() throws Exception {
        UUID orderId = UUID.randomUUID();
        orderStoreService.updateStatus(orderId, OrderStatus.SENT_WAITING_PROCESS);

        mockMvc.perform(get("/api/orders/status/{id}", orderId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SENT_WAITING_PROCESS"));
    }

    @Test
    @DisplayName("Deve retornar 400 se a quantidade for menor ou igual a zero")
    void deveRetornarBadRequestQuandoQuantidadeInvalida() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Mouse Gamer", 0, LocalDateTime.now());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestVO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400  se o produto estiver em branco")
    void deveRetornarBadRequestQuandoProdutoEmBranco() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "", 3, LocalDateTime.now());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestVO)))
                .andExpect(status().isBadRequest());
    }
}