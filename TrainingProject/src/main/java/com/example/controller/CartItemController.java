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
	
	@PostMapping("/add-to-cart/{userId}/{productId}")
	public ResponseEntity<ApiResponse<CartItem>> addToCart(@PathVariable Long userId, @PathVariable Long productId) {
		return cartItemService.addProductToCart(userId,productId);
	}
	
	@GetMapping("/get-by-user-and-product/{userId}/{productId}")
	public ResponseEntity<ApiResponse<CartItem>> getCartItems(@PathVariable Long userId, @PathVariable Long productId) {
		return cartItemService.getCartItems(userId, productId);
	}
	
	@GetMapping("/get-all-by-user/{userId}")
	public ResponseEntity<ApiResponse<List<CartItem>>> getItemsByUserId(@PathVariable Long userId) {
		return cartItemService.getItemsByUserId(userId);
	}
	
	@PutMapping("/increase-cart/{userId}/{productId}")
	public ResponseEntity<ApiResponse<CartItem>> increaseCart(@PathVariable Long userId, @PathVariable Long productId) {
		return cartItemService.increaseCart(userId, productId);
	}
	
	@DeleteMapping("/delete-by-cartid/{cartId}")
	public ResponseEntity<ApiResponse<String>> deleteCart(@PathVariable Long cartId) {
		return cartItemService.deleteCart(cartId);
	}
	
	@PutMapping("/decrease-cart/{userId}/{productId}")
	public ResponseEntity<ApiResponse<CartItem>> decreaseCart(@PathVariable Long userId, @PathVariable Long productId) {
		return cartItemService.decreaseCart(userId, productId);
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
