package com.example.test.vo;



import java.time.LocalDateTime;
import java.util.UUID;

import com.example.test.enums.OrderStatus;

/**
 * Objeto de valor para trafegar o status de processamento do pedido nas filas de retorno.
 */
public class OrderStatusVO {

    private UUID orderId;
    private OrderStatus status;
    private String errorMessage;
    private LocalDateTime processedAt;

    public OrderStatusVO() {
    }

    public static OrderStatusVO success(UUID orderId) {
        OrderStatusVO vo = new OrderStatusVO();
        vo.setOrderId(orderId);
        vo.setStatus(OrderStatus.SUCCESS);
        vo.setProcessedAt(LocalDateTime.now());
        return vo;
    }

    public static OrderStatusVO failure(UUID orderId, String errorMessage) {
        OrderStatusVO vo = new OrderStatusVO();
        vo.setOrderId(orderId);
        vo.setStatus(OrderStatus.FAILURE);
        vo.setErrorMessage(errorMessage);
        vo.setProcessedAt(LocalDateTime.now());
        return vo;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}