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
	
	@PostMapping("/add-to-cart/{userId}/{productId}/{quantity}")
	public ResponseEntity<ApiResponse<CartItem>> addToCart(@PathVariable Long userId, @PathVariable Long productId, @PathVariable int quantity) {
		return cartItemService.addProductToCart(userId,productId, quantity);
	}
	
	@GetMapping("/get-by-user-and-product/{userId}/{productId}")
	public ResponseEntity<ApiResponse<CartItem>> getCartItems(@PathVariable Long userId, @PathVariable Long productId) {
		return cartItemService.getCartItems(userId, productId);
	}
	
	@GetMapping("/get-all-by-user/{userId}")
	public ResponseEntity<ApiResponse<List<CartItem>>> getItemsByUserId(@PathVariable Long userId) {
		return cartItemService.getItemsByUserId(userId);
	}
	
	@PutMapping("/update-cart/{userId}/{productId}/{newQuantity}")
	public ResponseEntity<ApiResponse<CartItem>> updateCart(@PathVariable Long userId, @PathVariable Long productId, @PathVariable int newQuantity) {
		return cartItemService.updateCart(userId, productId, newQuantity);
	}

	@DeleteMapping("/delete-all-by-userid/{userId}")
	public ResponseEntity<ApiResponse<List<CartItem>>> deleteitems(@PathVariable Long userId) {
		return cartItemService.deleteAllbyUserId(userId);
	}
	
	@DeleteMapping("/delete-cart/{userId}/{productId}")
	public ResponseEntity<ApiResponse<CartItem>> deleteFromCart(@PathVariable Long userId, @PathVariable Long productId) {
		return cartItemService.deleteUserAndProduct(userId, productId);
	}
}