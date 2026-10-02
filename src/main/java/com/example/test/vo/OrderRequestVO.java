package com.example.test.vo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderRequestVO {

    @NotNull(message = "{order.id.required}")
    private UUID id;

    @NotBlank(message = "{order.product.required}")
    private String product;

    @Min(value = 1, message = "{order.quantity.min}")
    private int quantity;

    private LocalDateTime createdAt = LocalDateTime.now();

    public OrderRequestVO() {
    }

    public OrderRequestVO(UUID id, String product, int quantity, LocalDateTime createdAt) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}