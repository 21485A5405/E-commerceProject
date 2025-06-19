package com.example.service;

import java.util.List;

import com.example.model.CartItem;

public interface CartItemService {
	
	public String addProductToCart(Long userId,Long productId, int quantity);
	
	public CartItem getCartItems(Long userId, Long productId);
	
	public List<CartItem> getAllCartItems();
	
	public String deleteUserAndProduct(Long userId, Long productId);
	
	public List<CartItem> getItemsByUserId(Long userId);

	public String updateQuantity(Long userId, Long productId, int newQuantity);

	public String deleteAllbyUserId(Long userId);


}
