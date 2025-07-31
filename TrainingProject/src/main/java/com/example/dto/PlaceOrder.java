package com.example.dto;

import com.example.model.PaymentMethod;

public class PlaceOrder {
	
	private Long userId;
	private Long productId;
	private Long addressId;
	private PaymentMethod paymentType;
	private int quantity;
	
	public int getQuantity() {
		return quantity;
	}

	public Long getUserId() {
		return userId;
	}
	
	public Long getProductId() {
		return productId;
	}
	
	public Long getAddressId() {
		return addressId;
	}
	
	public PaymentMethod getPaymentType() {
		return paymentType;
	}

}
