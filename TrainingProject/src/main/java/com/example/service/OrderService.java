package com.example.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.controller.ApiResponse;
import com.example.dto.GetOrders;
import com.example.dto.PlaceOrder;
import com.example.enums.OrderStatus;
import com.example.enums.PaymentStatus;
import com.example.model.OrderProduct;

public interface OrderService {
	
	public ResponseEntity<List<GetOrders>> getOrderByUser(Long userId);

	public ResponseEntity<ApiResponse<OrderProduct>> cancelOrder(Long orderId);

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getByUserIdAndProductId(Long userId, Long productId);

	public ResponseEntity<ApiResponse<List<GetOrders>>> getAllOrders();

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderStatus(OrderStatus status);

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderByPayment(PaymentStatus paymentStatus);

	public ResponseEntity<ApiResponse<List<GetOrders>>> placeOrder(List<PlaceOrder> orderDetails);

	public ResponseEntity<ApiResponse<OrderProduct>> updateOrderStatus(Long orderId, OrderStatus status);

	public ResponseEntity<List<GetOrders>> getByOrderId(Long orderId);

	public ResponseEntity<ApiResponse<OrderProduct>> updatePaymentStatus(Long orderId, PaymentStatus status);
	

}
