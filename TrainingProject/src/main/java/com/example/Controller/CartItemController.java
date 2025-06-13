package com.example.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.Entity.CartItem;
import com.example.Service.CartItemService;

public class CartItemController {
	
	private CartItemService cartItemService;
	
	public CartItemController(CartItemService cartItemService) {
		this.cartItemService = cartItemService;	
	}
	

	@PostMapping("/addtocart/{userId}/{productId}/{quantity}")
	public Object addProduct(@PathVariable Long userId, @PathVariable Long productId, @PathVariable int quantity) {

		return cartItemService.addProduct(userId,productId, quantity);
		
	}
	
	@GetMapping("/getbyuserandproduct/{userId}/{productId}")
	public CartItem getCartItems(@PathVariable Long userId, @PathVariable Long productId) {
		
		return cartItemService.getCartItems(userId, productId);
		
	}
	
	@DeleteMapping("/deleteitems/{userId}/{productId}")
	public String deleteFromCart(@PathVariable Long userId, @PathVariable Long productId) {
		String message = cartItemService.deleteItem(userId, productId);
		return message;
	}
	
	@GetMapping("/getitemsbyuser/{userId}")
	public List<CartItem> getItemsByUserId(@PathVariable Long userId) {
		return cartItemService.getItemsByUserId(userId);
	}
	
	@GetMapping("getallitems")
	public List<CartItem> getAll(CartItem cartItem) {
		
		return cartItemService.getAllCartItems(cartItem);
	}

}
