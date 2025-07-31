package com.example.service;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;

import com.example.controller.ApiResponse;
import com.example.model.CartItem;

import jakarta.transaction.Transactional;

public interface CartItemService {
	
	public ResponseEntity<ApiResponse<CartItem>> addProductToCart(Long userId,Long productId);
	
	public ResponseEntity<ApiResponse<CartItem>> getCartItems(Long userId, Long productId);
	
	public ResponseEntity<ApiResponse<CartItem>> deleteUserAndProduct(Long userId, Long productId);
	
	public ResponseEntity<ApiResponse<List<CartItem>>> getItemsByUserId(Long userId);

	@Transactional
	@Modifying
	public ResponseEntity<ApiResponse<List<CartItem>>> deleteAllbyUserId(Long userId);

	
	public ResponseEntity<ApiResponse<CartItem>> increaseCart(Long userId, Long productId);

	public ResponseEntity<ApiResponse<CartItem>> decreaseCart(Long userId, Long productId);

	public ResponseEntity<ApiResponse<String>> deleteCart(Long cartId);
}
