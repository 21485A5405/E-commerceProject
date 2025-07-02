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
import com.example.model.AdminPermissions;
import com.example.model.CartItem;
import com.example.model.Product;
import com.example.model.Role;
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
	
	public CartItemServiceImpl(CartItemRepo cartItemRepo, CurrentUser currentUser, ProductRepo productRepo, UserRepo userRepo) {
		this.cartItemRepo = cartItemRepo;
		this.productRepo = productRepo;
		this.userRepo = userRepo;
		this.currentUser = currentUser;
		
	}
	
	public ResponseEntity<ApiResponse<CartItem>> addProductToCart(Long userId,Long productId, int quantity) {
		
		Optional<Product> p = productRepo.findById(productId);
		
		Optional<User> u = userRepo.findById(userId);
		
		Optional<CartItem> itemExist = cartItemRepo.findByUserAndProduct(userId, productId);
		
		if(!p.isPresent()) {
			throw new ProductNotFoundException("Product Not Found to Add Into Cart");
		}else if(!u.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}
		User currUser = Optional.ofNullable(currentUser.getUser())
			    .orElseThrow(() -> new UnAuthorizedException("Please login to access this resource"));
		
		if(currUser.getUserId()!= userId) {
			throw new UnAuthorizedException("User Not Authorized to Add Product Into Another Account");
		}
		
		Product product =p.get();
		CartItem cartItem = new CartItem();
		if(quantity <=0) {
			throw new CustomException("Quantity Cannot be Less than Zero");
		}
		if(product.getProductQuantity() == 0) {
			throw new CustomException("Out Of Stock");
		}
		if(product.getProductQuantity()<=quantity) {
			
			throw new CustomException("Enough Quantity Selected , We have "+product.getProductQuantity()+" Items available. Please Selcct Under "+product.getProductQuantity()+" as Quantity");
		}
		
		if(itemExist.isPresent()) {
			
			cartItem = itemExist.get();
			cartItem.setProductQuantity(quantity+cartItem.getProductQuantity());
			cartItem.setTotalPrice(cartItem.getProductQuantity()*product.getProductPrice());
			cartItemRepo.save(cartItem);
			ApiResponse<CartItem> response = new ApiResponse<>();
			response.setData(cartItem);
			response.setMessage("CartItems Updated Into Cart Successfully");
			return ResponseEntity.ok(response);

		}else {
			cartItem = new CartItem();
			User user = u.get();			
			cartItem.setUser(user);
			cartItem.setProduct(product);
			cartItem.setProductQuantity(quantity);
			cartItem.setTotalPrice(quantity*product.getProductPrice());
			
			cartItemRepo.save(cartItem);
			ApiResponse<CartItem> response = new ApiResponse<>();
			response.setData(cartItem);
			response.setMessage("New Items Added Into Cart Successfully");
			return ResponseEntity.ok(response);
		}
	}

	public ResponseEntity<ApiResponse<CartItem>> getCartItems(Long userId, Long productId) {
		
		Optional<CartItem> c = cartItemRepo.findByUserAndProduct(userId, productId);
		if(!c.isPresent()) {
			throw new UserNotFoundException("User with respective Product Not Found In Cart");
		}
		User currUser = Optional.ofNullable(currentUser.getUser())
			    .orElseThrow(() -> new UnAuthorizedException("Please login to access this resource"));
		if(currUser.getUserId()!= userId) {
			throw new UnAuthorizedException("Not Authorized To See Another User Cart Details");
		}
		
			CartItem cartItem = c.get();
			ApiResponse<CartItem> response = new ApiResponse<>();
			response.setData(cartItem);
			response.setMessage("CartItem Details");
			return ResponseEntity.ok(response);
	}

	@Transactional
	public ResponseEntity<ApiResponse<CartItem>> deleteUserAndProduct(Long userId, Long productId) {
		
		Optional<CartItem> exists = cartItemRepo.findByUserAndProduct(userId, productId);
		if (!exists.isPresent()) {
		    throw new ProductNotFoundException("No Items Found For That Product ID and User ID to Delete");
		}
		User currUser = Optional.ofNullable(currentUser.getUser())
			    .orElseThrow(() -> new UnAuthorizedException("Please login to access this resource"));
		if(currUser.getUserId()!= userId) {
			throw new UnAuthorizedException("Not Authorized to Delete Another User Cart Details");
		}
		
		cartItemRepo.deleteByUserAndProduct(userId, productId);
		CartItem cartItem = exists.get();
		ApiResponse<CartItem> response = new ApiResponse<>();
		response.setData(cartItem);
		response.setMessage("Item Deleted From the cart");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<CartItem>>> getItemsByUserId(Long userId) {
		
		List<CartItem> cartItems = cartItemRepo.findByUserId(userId);
		if(cartItems.isEmpty()) {
			throw new UserNotFoundException("User Not Found");
		}
		User currUser = currentUser.getUser();
		if(currUser.getUserId()!= userId) {
			throw new UnAuthorizedException("Not Authorized To See Another User Cart Details");
		}
		
		ApiResponse<List<CartItem>> response = new ApiResponse<>();
		response.setData(cartItems);
		response.setMessage("CartItem of User"+userId);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<CartItem>> updateQuantity(Long userId, Long productId, int newQuantity) {
		
		Optional<CartItem> c = cartItemRepo.findByUserAndProduct(userId, productId);
		Optional<Product> p = productRepo.findById(productId);
		
		if(!c.isPresent() || !p.isPresent()) {
			
			throw new ProductNotFoundException("No Product Found ");
		}
		User currUser = currentUser.getUser();
		if(currUser.getUserId()!= userId) {
			throw new UnAuthorizedException("Not Authorized To Update Another User Cart Details");
		}
		
		CartItem cartItem = c.get();
		Product product = p.get();
		cartItem.setProductQuantity(newQuantity);
		cartItem.setTotalPrice(newQuantity*product.getProductPrice());
		cartItemRepo.save(cartItem);
		ApiResponse<CartItem> response = new ApiResponse<>();
		response.setData(cartItem);
		response.setMessage("Quantity Updated Successfully");
		return ResponseEntity.ok(response);
	}

	@Transactional
	public ResponseEntity<ApiResponse<List<CartItem>>> deleteAllbyUserId(Long userId) {
		
		List<CartItem> c= cartItemRepo.findByUserId(userId);
		
		if(c.isEmpty()) {
			throw new UserNotFoundException("User Not Found");
		}
		User currUser = currentUser.getUser();
		if(currUser.getUserId()!= userId) {
			throw new UnAuthorizedException("Not Authorized To Delete Another User Cart Details");
		}
		
		cartItemRepo.deleteAllByUser(userId);
		ApiResponse<List<CartItem>> response = new ApiResponse<>();
		response.setData(c);
		response.setMessage("User "+userId+" Related Items Deleted From Cart Successfully");
		return ResponseEntity.ok(response);
	}

}
