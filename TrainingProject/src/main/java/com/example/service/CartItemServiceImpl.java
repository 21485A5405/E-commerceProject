package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.exceptionfile.CustomException;
import com.example.exceptionfile.ProductNotFoundException;
import com.example.exceptionfile.UserNotFoundException;
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
	
	public CartItemServiceImpl(CartItemRepo cartItemRepo, ProductRepo productRepo, UserRepo userRepo) {
		this.cartItemRepo = cartItemRepo;
		this.productRepo = productRepo;
		this.userRepo = userRepo;
		
	}
	
	public String addProductToCart(Long userId,Long productId, int quantity) {
		
		Optional<Product> p = productRepo.findById(productId);
		
		Optional<User> u = userRepo.findById(userId);
		
		Optional<CartItem> itemExist = cartItemRepo.findByUser_UserIdAndProduct_ProductId(userId, productId);
		
		if(!p.isPresent()) {
			throw new ProductNotFoundException("Product Not Found/ Exists to Add Into Cart");
		}else if(!u.isPresent()) {
			throw new UserNotFoundException("User Not Found");
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

		}else {
			
			cartItem = new CartItem();
			User user = u.get();			
			cartItem.setUser(user);
			cartItem.setProduct(product);
			cartItem.setProductQuantity(quantity);
			cartItem.setTotalPrice(quantity*product.getProductPrice());
			
			cartItemRepo.save(cartItem);
			System.out.println("Product Item Added Into Cart");
		}
			return "Item Added into Cart Successfully";
	}

	public CartItem getCartItems(Long userId, Long productId) {
		
		Optional<CartItem> c = cartItemRepo.findByUser_UserIdAndProduct_ProductId(userId, productId);
		if(!c.isPresent()) {
			throw new UserNotFoundException("User with respective Product Not Found In Cart");
		}
			CartItem cartItem = c.get();
		return cartItem;
	}

	@Transactional
	public String deleteUserAndProduct(Long userId, Long productId) {
		
		Optional<CartItem> c = cartItemRepo.findByUser_UserIdAndProduct_ProductId(userId, productId);
		if (!c.isPresent()) {
		    throw new ProductNotFoundException("No Items Found For That Product ID and User ID to Delete");
		}
		cartItemRepo.deleteByUser_UserIdAndProduct_ProductId(userId, productId);
		return "Item Deleted From the cart";
	}

	public List<CartItem> getAllCartItems(CartItem cartItem) {
		
		return cartItemRepo.findAll();
	}

	public List<CartItem> getItemsByUserId(Long userId) {
		
		List<CartItem> cartItems = cartItemRepo.findByUser_UserId(userId);
		if(cartItems.isEmpty()) {
			throw new UserNotFoundException("User Not Found");
		}
		return cartItems;
	}

	public String updateQuantity(Long userId, Long productId, int newQuantity) {
		
		Optional<CartItem> c = cartItemRepo.findByUser_UserIdAndProduct_ProductId(userId, productId);
		Optional<Product> p = productRepo.findById(productId);
		
		if(!c.isPresent()) {
			
			throw new ProductNotFoundException("No Product Found ");
		}
		CartItem cartItem = c.get();
		Product product = p.get();
		cartItem.setProductQuantity(newQuantity);
		cartItem.setTotalPrice(newQuantity*product.getProductPrice());
		cartItemRepo.save(cartItem);
		return "Quantity Updated Successfully";
	}

	public String deleteAllbyUserId(Long userId) {
		List<CartItem> c= cartItemRepo.findByUser_UserId(userId);
		
		if(!c.isEmpty()) {
			throw new UserNotFoundException("User Not Found");
		}
		cartItemRepo.deleteAllByUser_UserId(userId);
		return "User "+userId+" Related Items Deleted From Cart Successfully";
	}

}
