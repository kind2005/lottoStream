package com.example.model;

import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = {"timestamp"})
public class OrderBookVO {
	private String exchange;

	private String timestamp;
	private String payment_currency;

	private String order_currency;
	private List<Order> bids = Collections.emptyList();
	private List<Order> asks = Collections.emptyList();
}
