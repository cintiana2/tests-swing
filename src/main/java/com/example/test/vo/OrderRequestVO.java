package com.example.test.vo;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class OrderRequestVO {

	@NotNull
	private UUID id;

	@NotBlank(message = "O produto não pode estar vazio.")
	private String product;

	@Min(value = 1, message = "A quantidade deve ser maior que zero.")
	private int quantity;

	private LocalDateTime dataCriacao;

	public OrderRequestVO() {
		this.dataCriacao = LocalDateTime.now();
	}

	public OrderRequestVO(UUID id, String product, int quantity, LocalDateTime dataCriacao) {
		this.id = id;
		this.product = product;
		this.quantity = quantity;
		this.dataCriacao = dataCriacao != null ? dataCriacao : LocalDateTime.now();
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

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
}