package com.example.Controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Entity.OrderProduct;
import com.example.Service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
	
	@Autowired
	private OrderService orderService;	
	
	@PostMapping("/placeorder/{userId}/{productId}")
	public String placeOrder(@PathVariable Long userId, @PathVariable Long productId) {

		return orderService.placeOrder(userId, productId);
	}
	
	@GetMapping("/getorderbyuserid/{userId}")
	public List<OrderProduct> getOrderByUserId(@PathVariable Long userId) {
		
		return orderService.getOrderByUser(userId);
	}

}
