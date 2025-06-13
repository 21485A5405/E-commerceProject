package com.example.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Entity.CartItem;
import com.example.Entity.OrderProduct;
import com.example.Entity.Product;
import com.example.Entity.User;
import com.example.Repository.CartItemRepo;
import com.example.Repository.OrderRepo;
import com.example.Repository.ProductRepo;
import com.example.Repository.UserRepo;
import com.example.exceptionfile.ProductNotFoundException;
import com.example.exceptionfile.UserNotFoundException;

@Service
public class OrderService {
	
	
	public OrderRepo orderRepo;
	private UserRepo userRepo;
	private ProductRepo productRepo;
	private CartItemRepo cartItemRepo;
	
	public OrderService(UserRepo userRepo, CartItemRepo cartItemRepo, ProductRepo productRepo, OrderRepo orderRepo) {
		this.userRepo = userRepo;
		this.cartItemRepo = cartItemRepo;
		this.productRepo = productRepo;
		this.orderRepo = orderRepo;
	}
	

	public String placeOrder(Long userId, Long productId) {

	    Optional<User> findUser = userRepo.findById(userId);
	    Optional<Product> findProduct = productRepo.findById(productId);
	    Optional<CartItem> cart = cartItemRepo.findByUser_UserIdAndProduct_ProductId(userId, productId);

	    if (!findUser.isPresent()) {
	        throw new UserNotFoundException("User Not Found");
	    }
	    
	    if (!findProduct.isPresent()) {
	        throw new ProductNotFoundException("Product Not Available");
	    }

	    if (!cart.isPresent()) {
	        throw new ProductNotFoundException("Please Add Product into Cart to place Order");
	    }else {
	    	 CartItem cartItem = cart.get();
	 	    User user = findUser.get();
	 	    Product product = findProduct.get();
	 	    OrderProduct orderProduct = new OrderProduct();
	 	    orderProduct.setProduct(product);
	 	    orderProduct.setUser(user);
	 	    orderProduct.setShippingAddress(user.getShippingAddress());
	 	    orderProduct.setTotalPrice(cartItem.getTotalPrice());
	 	    orderProduct.setOrderStatus("Order Placed");
	 	    orderProduct.setPaymentStatus("Payment Done");

	 	    orderRepo.save(orderProduct);
	 	    cartItemRepo.delete(cartItem);
	    }
	   
	    return "OrderPlaced";
	}

	public List<OrderProduct> getOrderByUser(Long userId) {
		
		List<OrderProduct> o = orderRepo.findByUser_UserId(userId);
		
		if(o.isEmpty()) {
			throw new UserNotFoundException("No Order Details Found with Given User ID");
		}
		
		return o;
	}

}