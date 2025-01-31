package com.example.model;

import lombok.Data;

@Data
public class Order {
	private String quantity;
	private String price;
	private String notice;

	public Order(String price, String quantity) {
		this.price = price;
		this.quantity = quantity;
	}
}
