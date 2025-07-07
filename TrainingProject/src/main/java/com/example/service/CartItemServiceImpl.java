package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.authentication.CurrentUser;
import com.example.controller.ApiResponse;
import com.example.exception.CustomException;
import com.example.exception.ProductNotFoundException;
import com.example.exception.UnAuthorizedException;
import com.example.exception.UserNotFoundException;
import com.example.model.CartItem;
import com.example.model.Product;
import com.example.model.User;
import com.example.repo.CartItemRepo;
import com.example.repo.ProductRepo;
import com.example.repo.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class CartItemServiceImpl implements CartItemService{

	private CartItemRepo cartItemRepo;
	private ProductRepo productRepo;
	private UserRepo userRepo;
	private CurrentUser currentUser;
	
	public CartItemServiceImpl(CartItemRepo cartItemRepo, CurrentUser currentUser, 
									ProductRepo productRepo, UserRepo userRepo) {
		this.cartItemRepo = cartItemRepo;
		this.productRepo = productRepo;
		this.userRepo = userRepo;
		this.currentUser = currentUser;
		
	}
	
	public ResponseEntity<ApiResponse<CartItem>> addProductToCart(Long productId, int quantity) {
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		Optional<CartItem> exists = cartItemRepo.findByUserAndProduct(currUser.getUserId(), productId);
		Optional<Product> p = productRepo.findById(productId);
		Optional<User> u = userRepo.findById(currUser.getUserId());
		
		if(!u.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}
		if(!p.isPresent()) {
			throw new ProductNotFoundException("Product Not Found to Add Into Cart");
		}
		Product product =p.get();
		User user = u.get();	
		if(product.getProductQuantity() == 0) {
			throw new CustomException("Product Out Of Stock");
		}
		if(quantity <=0) {
			throw new CustomException("Quantity Cannot be Less than Zero");
		}
		
		if(product.getProductQuantity()<quantity) {
			
			throw new CustomException("Enough Quantity Selected , We have "
					+product.getProductQuantity()+" Items available. Please Selcct Under "
							+product.getProductQuantity()+" as Quantity");
		}
		CartItem cartItem = null;
		if(exists.isPresent()) { // only increase the existing quantity 
			cartItem = exists.get();
			cartItem.setProductQuantity(cartItem.getProductQuantity()+quantity);
			
		}else {
			cartItem = new CartItem();
			cartItem.setUser(user);
			cartItem.setProduct(product);
			cartItem.setProductQuantity(quantity);
			cartItem.setTotalPrice(quantity*product.getProductPrice());
			cartItemRepo.save(cartItem);
		}
		ApiResponse<CartItem> response = new ApiResponse<>();
		response.setData(cartItem);
		response.setMessage("Item Added Into Cart Successfully");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<CartItem>> getCartItems(Long productId) {
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		Optional<CartItem> c = cartItemRepo.findByUserAndProduct(currUser.getUserId(), productId);
		if(!c.isPresent()) {
			throw new UserNotFoundException("User with respective Product Not Found In Cart");
		}
		
			CartItem cartItem = c.get();
			ApiResponse<CartItem> response = new ApiResponse<>();
			response.setData(cartItem);
			response.setMessage("CartItem Details");
			return ResponseEntity.ok(response);
	}

	@Transactional
	public ResponseEntity<ApiResponse<CartItem>> deleteUserAndProduct(Long productId) {
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		Optional<CartItem> exists = cartItemRepo.findByUserAndProduct(currUser.getUserId(), productId);
		if (!exists.isPresent()) {
		    throw new ProductNotFoundException("No Items Found For That Product ID and User ID to Delete");
		}
		
		cartItemRepo.deleteByUserAndProduct(currUser.getUserId(), productId);
		CartItem cartItem = exists.get();
		ApiResponse<CartItem> response = new ApiResponse<>();
		response.setData(cartItem);
		response.setMessage("Item Deleted From the cart");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<CartItem>>> getItemsByUser() {

		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		Optional<User> exists = userRepo.findById(currUser.getUserId());
		if(!exists.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}
		List<CartItem> cartItems = cartItemRepo.findByUserId(currUser.getUserId());
		if(cartItems.isEmpty()) {
			throw new UserNotFoundException("User Cart Is empty");
		}
		
		ApiResponse<List<CartItem>> response = new ApiResponse<>();
		response.setData(cartItems);
		response.setMessage("CartItem of User"+currUser.getUserId());
		return ResponseEntity.ok(response);
	}

	@Transactional
	public ResponseEntity<ApiResponse<List<CartItem>>> deleteAllbyUser() {

		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		Optional<User> exists = userRepo.findById(currUser.getUserId());
		if(!exists.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}
		List<CartItem> c= cartItemRepo.findByUserId(currUser.getUserId());
		
		if(c.isEmpty()) {
			throw new UserNotFoundException("User Cart Is empty");
		}
		
		cartItemRepo.deleteAllByUser(currUser.getUserId());
		ApiResponse<List<CartItem>> response = new ApiResponse<>();
		response.setData(c);
		response.setMessage("User "+currUser.getUserId()+" Related Items Deleted From The Cart Successfully");
		return ResponseEntity.ok(response);
	}

	@Transactional
	public ResponseEntity<ApiResponse<CartItem>> updateCart(Long productId, int newQuantity) {
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		Optional<CartItem> exists = cartItemRepo.findByUserAndProduct(currUser.getUserId(), productId);
		Optional<Product> p = productRepo.findById(productId);
		Optional<User> u = userRepo.findById(currUser.getUserId());
		
		if(!u.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}
		if(!p.isPresent()) {
			throw new ProductNotFoundException("Product Not Found to Add to Cart");
		}
		Product product =p.get();
		if(product.getProductQuantity() == 0) {
			throw new CustomException("Product Out Of Stock");
		}
		if(newQuantity <=0) {
			throw new CustomException("Quantity Cannot be Less than Zero");
		}
		
		if(product.getProductQuantity()<newQuantity) {
			
			throw new CustomException("Enough Quantity Selected , We have "
					+product.getProductQuantity()+" Items available. Please Selcct Under "
							+product.getProductQuantity()+" as Quantity");
		}
		CartItem cartItem = null;
		if(exists.isPresent()) { // update Existing quantity 
			cartItem = exists.get();
			cartItem.setProductQuantity(newQuantity);
			cartItem.setTotalPrice(newQuantity);
		}
		ApiResponse<CartItem> response = new ApiResponse<>();
		response.setData(cartItem);
		response.setMessage("CartItems Updated Successfully");
		return ResponseEntity.ok(response);
	}
}
