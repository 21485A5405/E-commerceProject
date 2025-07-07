package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.CartItem;
import com.example.service.CartItemService;

@RestController
@RequestMapping("/cart")
public class CartItemController {
	
	
	private CartItemService cartItemService;
	
	public CartItemController(CartItemService cartItemService) {
		this.cartItemService = cartItemService;	
	}
	
	@PostMapping("/add-to-cart/{productId}/{quantity}")
	public ResponseEntity<ApiResponse<CartItem>> addToCart(@PathVariable Long productId, @PathVariable int quantity) {
		return cartItemService.addProductToCart(productId, quantity);
		
	}
	
	@GetMapping("/get-by-user-and-product/{productId}")
	public ResponseEntity<ApiResponse<CartItem>> getCartItems(@PathVariable Long productId) {
		return cartItemService.getCartItems(productId);
		
	}
	
	@GetMapping("/get-all-by-user")
	public ResponseEntity<ApiResponse<List<CartItem>>> getItemsByUserId() {
		return cartItemService.getItemsByUser();
	}
	
	@PutMapping("/update-cart/{productId}/{newQuantity}")
	public ResponseEntity<ApiResponse<CartItem>> updateCart(@PathVariable Long productId, @PathVariable int newQuantity) {
		return cartItemService.updateCart(productId, newQuantity);
	}

	@DeleteMapping("/delete-all-by-user")
	public ResponseEntity<ApiResponse<List<CartItem>>> deleteitems() {
		return cartItemService.deleteAllbyUser();
	}
	
	@DeleteMapping("/delete-cart/{productId}")
	public ResponseEntity<ApiResponse<CartItem>> deleteFromCart(@PathVariable Long productId) {
		return cartItemService.deleteUserAndProduct(productId);
	}
}