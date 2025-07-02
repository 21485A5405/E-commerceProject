package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.OrderProduct;
import com.example.model.OrderStatus;
import com.example.model.PaymentStatus;
import com.example.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
	
	private OrderService orderService;
		
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping("/place-order/{userId}/{productId}/{quantity}/{addressId}")
	public ResponseEntity<ApiResponse<OrderProduct>> placeOrder(@PathVariable Long userId, @PathVariable Long productId, @PathVariable int quantity, @PathVariable Long addressId) {
		return orderService.placeOrder(userId, productId, quantity, addressId);
	}
	
	@GetMapping("/get-by-user/{userId}")
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderByUserId(@PathVariable Long userId) {
		return orderService.getOrderByUser(userId);
	}
	
	@GetMapping("/get-order/{userId}/{productId}")
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderDetails(@PathVariable Long userId, @PathVariable Long productId) {
		return orderService.getByUserIdAndProductId(userId, productId);
	}
	
	@GetMapping("/get-by-order-status/{status}")
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrders(@PathVariable OrderStatus status) {
		return orderService.getOrderStatus(status);
	}
	
	@GetMapping("/get-by-payment-status/{paymentStatus}")
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrder(@PathVariable PaymentStatus paymentStatus) {
		return orderService.getOrderByPayment(paymentStatus);
	}
	
	@GetMapping("/getall")
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getAll() {
		return orderService.getAllOrders();
	}
	
	@DeleteMapping("/cancel-order/{userId}/{productId}/{quantity}")
	public ResponseEntity<ApiResponse<OrderProduct>> cancelOrder(@PathVariable Long userId, @PathVariable Long productId,@PathVariable int quantity) {
		return orderService.cancelOrder(userId, productId, quantity);
	}
}