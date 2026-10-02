package com.example.test.swing.model;

import java.util.UUID;

public class OrderModel {

    private final UUID id;
    private final String product;
    private final int quantity;
    private String status;

    public OrderModel(UUID id, String product, int quantity, String status) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}