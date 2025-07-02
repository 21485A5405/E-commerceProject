package com.example.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.controller.ApiResponse;
import com.example.model.OrderProduct;
import com.example.model.OrderStatus;
import com.example.model.PaymentStatus;

public interface OrderService {
	
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderByUser(Long userId);
	
	public ResponseEntity<ApiResponse<OrderProduct>> placeOrder(Long userId, Long productId, int quantity, Long addressId);

	public ResponseEntity<ApiResponse<OrderProduct>> cancelOrder(Long userId, Long productId, int quantity);

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getByUserIdAndProductId(Long userId, Long productId);

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getAllOrders();

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderStatus(OrderStatus status);

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderByPayment(PaymentStatus paymentStatus);
	

}
