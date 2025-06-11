package com.example.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Service.OrderService;

@RestController
@RequestMapping("/place")
public class OrderController {
	
	@Autowired
	private OrderService orderService;	
	
	@PostMapping("/order/{userId}/{productId}")
	public String placeOrder(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId) {

		return orderService.placeOrder(userId, productId);
	}
	
	

}
