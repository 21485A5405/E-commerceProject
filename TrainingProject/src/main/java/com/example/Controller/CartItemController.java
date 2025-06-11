package com.example.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Entity.CartItem;
import com.example.Entity.Product;
import com.example.Service.CartItemService;
import com.example.Service.ProductService;

@RestController
@RequestMapping("/add")
public class CartItemController {
	
	private CartItemService cartItemService;
	
	public CartItemController(CartItemService cartItemService) {
		this.cartItemService = cartItemService;	
	}
	
	
	@PostMapping("/tocart/{userId}/{productId}/{Quantity}")
	public Object addProduct(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId, @PathVariable("Quantity") int quantity) {
		
		return cartItemService.addProduct(userId,productId, quantity);
		
	}

}
