package com.example.dto;

import java.time.LocalDateTime;

import com.example.enums.OrderStatus;
import com.example.enums.PaymentStatus;
import com.example.model.OrderItem;
import com.example.model.OrderProduct;

public class GetOrders {
	
	private Long orderId;
	private Long userId;
	private LocalDateTime orderDate;
	private OrderStatus orderStatus;
	private String shippingAddress;
	private double totalPrice;
	private Long ProductId;
	private int productQuantity;
	private PaymentStatus paymentStatus;
	
	public GetOrders(OrderProduct orderProduct, OrderItem orderItems) {
		this.orderId = orderProduct.getOrderId();
		this.userId = orderProduct.getUser().getUserId();
		this.orderDate = orderProduct.getOrderDate();
		this.orderStatus = orderProduct.getOrderStatus();
		this.paymentStatus = orderProduct.getPaymentStatus();
		this.ProductId = orderItems.getProduct().getProductId();
		this.shippingAddress  = orderProduct.getShippingAddress();
		this.totalPrice = orderProduct.getTotalPrice();
		this.productQuantity = orderItems.getQuantity();
		
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Long getProductId() {
		return ProductId;
	}

	public void setProductId(Long productId) {
		ProductId = productId;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	

}
