package com.example.service;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;

import com.example.controller.ApiResponse;
import com.example.model.CartItem;

import jakarta.transaction.Transactional;

public interface CartItemService {
	
	public ResponseEntity<ApiResponse<CartItem>> addProductToCart(Long productId, int quantity);
	
	public ResponseEntity<ApiResponse<CartItem>> getCartItems(Long productId);
	
	public ResponseEntity<ApiResponse<CartItem>> deleteUserAndProduct(Long productId);
	
	public ResponseEntity<ApiResponse<List<CartItem>>> getItemsByUser();

	@Transactional
	@Modifying
	public ResponseEntity<ApiResponse<List<CartItem>>> deleteAllbyUser();

	public ResponseEntity<ApiResponse<CartItem>> updateCart(Long productId, int newQuantity);


}
