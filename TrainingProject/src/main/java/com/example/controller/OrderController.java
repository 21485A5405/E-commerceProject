package com.example.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.OrderProduct;
import com.example.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
	
	private OrderService orderService;
	
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}
	

	@PostMapping("/placeorder/{userId}/{productId}")
	public String placeOrder(@PathVariable Long userId, @PathVariable Long productId) {

		return orderService.placeOrder(userId, productId);
	}
	
	@GetMapping("/getbyuser/{userId}")
	public List<OrderProduct> getOrderByUserId(@PathVariable Long userId) {
		
		return orderService.getOrderByUser(userId);
	}
	
	@GetMapping("/getorder/{userId}/{productId}")
	public List<OrderProduct> getOrderDetails(@PathVariable Long userId, @PathVariable Long productId) {
		
		return orderService.getByUserIdAndProductId(userId, productId);
	}
	
	@GetMapping("/getorderstatus/{status}")
	public List<OrderProduct> getOrders(@PathVariable String status) {
		return orderService.getOrderStatus(status);
	}
	
	@GetMapping("/getbypayment{paymentStatus}")
	public List<OrderProduct> getOrder(String paymentStatus) {
		return orderService.getOrderByPayment(paymentStatus);
	}
	
	@GetMapping("/getall")
	public List<OrderProduct> getAll() {
		return orderService.getAllOrders();
	}
	@DeleteMapping("/cancelorder/{userId}/{productId}")
	public String cancelOrder(@PathVariable Long userId, @PathVariable Long productId) {
		String message = orderService.cancelOrder(userId, productId);
		return message;
	}
	
	
	
	
}
