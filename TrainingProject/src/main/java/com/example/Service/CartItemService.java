package com.example.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Entity.CartItem;
import com.example.Entity.Product;
import com.example.Entity.User;
import com.example.Repository.CartItemRepo;
import com.example.Repository.ProductRepo;
import com.example.Repository.UserRepo;
import com.example.exceptionfile.ProductNotFoundException;
import com.example.exceptionfile.UserNotFoundException;

@Service
public class CartItemService {
	
	private CartItemRepo cartItemRepo;
	private ProductRepo productRepo;
	private UserRepo userRepo;
	
	public CartItemService(CartItemRepo cartItemRepo, ProductRepo productRepo, UserRepo userRepo) {
		this.cartItemRepo = cartItemRepo;
		this.productRepo = productRepo;
		this.userRepo = userRepo;
		
	}
	
	public CartItem addProduct(Long userId,Long productId, int quantity) {
		
		Optional<Product> p = productRepo.findById(productId);
		
		Optional<User> u = userRepo.findById(userId);
		
		Optional<CartItem> itemExist = cartItemRepo.findByUser_UserIdAndProduct_ProductId(userId, productId);
		
		if(!p.isPresent()) {
			throw new ProductNotFoundException("Product Not Found to Add Into Cart");
		}else if(!u.isPresent()) {
			throw new UserNotFoundException("User Not Found");
		}
		Product product =p.get();
		CartItem cartItem = new CartItem();
		
		if(itemExist.isPresent()) {
			
			cartItem = itemExist.get();
			cartItem.setProductQuantity(quantity+cartItem.getProductQuantity());
			cartItem.setTotalPrice(cartItem.getProductQuantity()*product.getProductPrice());
			cartItemRepo.save(cartItem);
//			cartItemRepo.delete(cartItem);
			
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
			return cartItem;
	}
	
}
