package com.example.test.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.test.enums.OrderStatus;
import com.example.test.service.OrderStoreService;
import com.example.test.vo.OrderRequestVO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Testes de Integração E2E (RabbitMQ Real)")
class OrderRealIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderStoreService orderStoreService;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/orders";
    }

    @Test
    @DisplayName(" Deve receber 202 na chamada REST")
    void deveEnviarPedidoViaPostHttpComSucesso() {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Cadeira Ergonomica", 1, LocalDateTime.now());

        ResponseEntity<Map> postResponse = restTemplate.postForEntity(getBaseUrl(), requestVO, Map.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(postResponse.getBody()).isNotNull();
        assertThat(postResponse.getBody().get("orderId")).isEqualTo(orderId.toString());
    }

    @Test
    @DisplayName("Deve registar SENT_WAITING_PROCESS na Store antes do processamento")
    void deveRegistrarStatusInicialSentWaitingProcess() {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Mesa Digitalizadora", 1, LocalDateTime.now());

        restTemplate.postForEntity(getBaseUrl(), requestVO, Map.class);

        assertThat(orderStoreService.getStatus(orderId)).isEqualTo(OrderStatus.SENT_WAITING_PROCESS);
    }

    @Test
    @DisplayName("Deve processar a mensagem no RabbitMQ e atualizar para SUCCESS ou FAILURE")
    void deveConsumirDaFilaEAtualizarStatusFinal() throws InterruptedException {
        UUID orderId = UUID.randomUUID();
        OrderRequestVO requestVO = new OrderRequestVO(orderId, "Headset Wireless", 1, LocalDateTime.now());

        // 1. Envia o pedido
        restTemplate.postForEntity(getBaseUrl(), requestVO, Map.class);

        // 2. Aguarda o processamento assíncrono do consumidor RabbitMQ
        Thread.sleep(4000);

        // 3. Consulta via GET o resultado do processamento
        ResponseEntity<Map> getResponse = restTemplate.getForEntity(getBaseUrl() + "/status/" + orderId, Map.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();

        String statusFinal = (String) getResponse.getBody().get("status");
        assertThat(statusFinal).isIn(OrderStatus.SUCCESS.name(), OrderStatus.FAILURE.name());
    }
}