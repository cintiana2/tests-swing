package com.example.test.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderModel {

	private final UUID id;
	private final String product;
	private final int quantity;
	private final LocalDateTime dataCriacao;
	private String status;

	public OrderModel(UUID id, String product, int quantity, String status) {
		this(id, product, quantity, LocalDateTime.now(), status);
	}

	public OrderModel(UUID id, String product, int quantity, LocalDateTime dataCriacao, String status) {
		this.id = id;
		this.product = product;
		this.quantity = quantity;
		this.dataCriacao = dataCriacao;
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

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}