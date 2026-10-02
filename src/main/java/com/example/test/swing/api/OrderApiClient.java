package com.example.test.swing.api;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.test.swing.config.AppConfig;

public class OrderApiClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public OrderApiClient() {
        // Obtém obrigatoriamente a URL do arquivo de configuração
        this.baseUrl = AppConfig.getApiBaseUrl();
        this.restTemplate = new RestTemplate();
    }

    public boolean sendOrder(UUID id, String product, int quantity) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", id);
            payload.put("product", product);
            payload.put("quantity", quantity);
            payload.put("dataCriacao", LocalDateTime.now().toString());

            ResponseEntity<Void> response = restTemplate.postForEntity(baseUrl, payload, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    public String fetchOrderStatus(UUID orderId) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl + "/status/" + orderId, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("status");
            }
        } catch (Exception e) {
            // Trata indisponibilidade momentânea do serviço
        }
        return "UNKNOWN";
    }
}