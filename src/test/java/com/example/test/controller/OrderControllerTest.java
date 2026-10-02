package com.example.test.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.test.enums.OrderStatus;
import com.example.test.service.OrderService;
import com.example.test.vo.OrderRequestVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Deve receber o pedido, chamar OrderService e retornar 202 ")
    void createOrder_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Notebook", 2, LocalDateTime.now());

        when(orderService.createAndPublishOrder(any(OrderRequestVO.class))).thenReturn(orderId);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestVO)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()));

        verify(orderService).createAndPublishOrder(any(OrderRequestVO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad  quando a quantidade for invalida (<= 0)")
    void createOrder_ValidationError_InvalidQuantity() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Smartphone", 0, LocalDateTime.now());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestVO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar  400  quando o produto estiver em branco")
    void createOrder_ValidationError_BlankProduct() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "", 5, LocalDateTime.now());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestVO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve consultar o status do pedido")
    void getOrderStatus_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.getOrderStatus(eq(orderId))).thenReturn(OrderStatus.PROCESSING);

        mockMvc.perform(get("/api/orders/status/{id}", orderId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));

        verify(orderService).getOrderStatus(orderId);
    }
}