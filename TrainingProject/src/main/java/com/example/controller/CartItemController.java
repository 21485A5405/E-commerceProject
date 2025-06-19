package com.example.controller;

import java.util.List;

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
	
	@PostMapping("/addtocart/{userId}/{productId}/{quantity}")
	public String addToCart(@PathVariable Long userId, @PathVariable Long productId, @PathVariable int quantity) {

		String message = cartItemService.addProductToCart(userId,productId, quantity);
		return message;
		
	}
	
	@GetMapping("/getbyuserandproduct/{userId}/{productId}")
	public CartItem getCartItems(@PathVariable Long userId, @PathVariable Long productId) {
		
		return cartItemService.getCartItems(userId, productId);
		
	}
	
	@GetMapping("/getallbyuser/{userId}")
	public List<CartItem> getItemsByUserId(@PathVariable Long userId) {
		
		return cartItemService.getItemsByUserId(userId);
	}
	
	@GetMapping("getall")
	public List<CartItem> getAll() {
		
		return cartItemService.getAllCartItems();
	}

	@PutMapping("/updatecartquantity/{userId}/{productId}/{newQuantity}")
	public String updateQuantityInCart(@PathVariable Long userId, @PathVariable Long productId, @PathVariable int newQuantity) {
		
		String message = cartItemService.updateQuantity(userId, productId, newQuantity);
		return message;
	}
	
	@DeleteMapping("/deleteallbyuserid/{userId}")
	public String deleteitems(@PathVariable Long userId) {
		
		String message = cartItemService.deleteAllbyUserId(userId);
		return message;
	}
	
	@DeleteMapping("/deletecart/{userId}/{productId}")
	public String deleteFromCart(@PathVariable Long userId, @PathVariable Long productId) {
		
		String message = cartItemService.deleteUserAndProduct(userId, productId);
		return message;
	}
	
}
