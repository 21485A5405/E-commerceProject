package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.customannotations.ForOrders;
import com.example.dto.PlaceOrder;
import com.example.enums.AdminPermissions;
import com.example.enums.OrderStatus;
import com.example.enums.PaymentStatus;
import com.example.enums.Role;
import com.example.model.OrderProduct;
import com.example.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
	
	private OrderService orderService;
		
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping("/place-order")
	public ResponseEntity<ApiResponse<OrderProduct>> placeOrder(@RequestBody PlaceOrder orderDetails) {
		return orderService.placeOrder(orderDetails);	
	}
	
	@GetMapping("/get-by-user/{userId}")
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderByUserId(@PathVariable Long userId) {
		return orderService.getOrderByUser(userId);
	}
	
	@GetMapping("/get-order/{userId}/{productId}")
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderDetails(@PathVariable Long userId, @PathVariable Long productId) {
		return orderService.getByUserIdAndProductId(userId, productId);
	}
	
	@GetMapping("/get-by-orderstatus/{status}")
	@ForOrders(validPermissions = {AdminPermissions.Manager,AdminPermissions.Order_Manager},requiredRole = Role.ADMIN)
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrders(@PathVariable OrderStatus status) {
		return orderService.getOrderStatus(status);
	}
	
	@GetMapping("/get-by-paymentstatus/{paymentStatus}")	
	@ForOrders(validPermissions = {AdminPermissions.Manager,AdminPermissions.Order_Manager},requiredRole = Role.ADMIN)
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrder(@PathVariable PaymentStatus paymentStatus) {
		return orderService.getOrderByPayment(paymentStatus);
	}
	
	@GetMapping("/get-all")
	@ForOrders(validPermissions = {AdminPermissions.Manager,AdminPermissions.Order_Manager},requiredRole = Role.ADMIN)
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getAll() {
		return orderService.getAllOrders();
	}
	
	@PutMapping("update-orderstatus/{orderId}/{status}")
	@ForOrders(validPermissions = {AdminPermissions.Manager,AdminPermissions.Order_Manager},requiredRole = Role.ADMIN)
	public ResponseEntity<ApiResponse<OrderProduct>> updateOrderStatus(@PathVariable Long orderId, @PathVariable OrderStatus status) {
		return orderService.updateOrderStatus(orderId, status);
	}
	
	@DeleteMapping("/cancel-order/{userId}/{productId}/{quantity}")
	public ResponseEntity<ApiResponse<OrderProduct>> cancelOrder(@PathVariable Long userId, @PathVariable Long productId,@PathVariable int quantity) {
		return orderService.cancelOrder(userId, productId, quantity);
	}
}