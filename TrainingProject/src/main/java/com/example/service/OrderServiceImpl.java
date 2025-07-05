package com.example.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.DTO.PlaceOrder;
import com.example.authentication.CurrentUser;
import com.example.controller.ApiResponse;
import com.example.exception.CustomException;
import com.example.exception.ProductNotFoundException;
import com.example.exception.UnAuthorizedException;
import com.example.exception.UserNotFoundException;
import com.example.model.*;
import com.example.repo.AddressRepo;
import com.example.repo.CartItemRepo;
import com.example.repo.OrderRepo;
import com.example.repo.ProductRepo;
import com.example.repo.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService{
	
	
	public OrderRepo orderRepo;
	private UserRepo userRepo;
	private ProductRepo productRepo;
	private CartItemRepo cartItemRepo;
	private AddressRepo addressRepo;
	private CurrentUser currentUser;
	
	public OrderServiceImpl(UserRepo userRepo, CurrentUser currentUser, CartItemRepo cartItemRepo, ProductRepo productRepo, OrderRepo orderRepo, AddressRepo addressRepo) {
		this.userRepo = userRepo;
		this.cartItemRepo = cartItemRepo;
		this.productRepo = productRepo;
		this.orderRepo = orderRepo;
		this.addressRepo = addressRepo;
		this.currentUser = currentUser;
	}

	@Transactional
	public ResponseEntity<ApiResponse<OrderProduct>> placeOrder(PlaceOrder orderDetails) {
		
	    Optional<User> findUser = userRepo.findById(orderDetails.getUserId());
	    Optional<Product> findProduct = productRepo.findById(orderDetails.getProductId());
	    Optional<CartItem> cart = cartItemRepo.findByUserAndProduct(orderDetails.getUserId(), orderDetails.getProductId());
	    Optional<Address> addressExists = addressRepo.findById(orderDetails.getAddressId());
	    List<CartItem> productList = cartItemRepo.findAllByProduct(findProduct.get());
	    User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(currUser.getUserId()!= orderDetails.getUserId()) {
			throw new UnAuthorizedException("Not Authorized to Place Order with Another User ID");
		}
	    
	    if (!findUser.isPresent()) {
	        throw new UserNotFoundException("User Not Found");
	    }
	    if (!findProduct.isPresent()) {
	        throw new ProductNotFoundException("Product Not Available");
	    }
	    
	    if (!cart.isPresent()) {
	        throw new ProductNotFoundException("Please Add Product into Cart to place Order");
	    }
	    if(!addressExists.isPresent()) {
	    	throw new CustomException("No Address Found with Address ID" +orderDetails.getAddressId());
	    }
	    CartItem cartItem = cart.get();
	    if(cartItem.getProductQuantity()<orderDetails.getQuantity()) {
	    	throw new CustomException("Only "+cartItem.getProductQuantity()
					    	+" Items Added Into Cart Please Add "+(orderDetails.getQuantity()-cartItem.getProductQuantity())
					    	+" items to Place an Order");
	    }
	    
	    List<PaymentInfo> payment = findUser.get().getPaymentDetails();

	    if (payment == null || payment.isEmpty()) {
	        throw new CustomException("Payment Method Can Not be Empty");
	    }
	    boolean isValid = false;
	    
	    for (PaymentInfo info : payment) {
	        if (info.getPaymentMethod() == orderDetails.getPaymentType()) {
	        	isValid = true;
	        }
	    }
        if(!isValid) {
        	throw new UnAuthorizedException("Selected Payment Method Not Available In Your Account, "
        			+ "Available Payments in Your Account "+findUser.get().displayPayments());
        }
 	    OrderProduct order = new OrderProduct();
 	    OrderItem orderItem = new OrderItem();
	    if(cart.isPresent()) {	 
	 	    User user = findUser.get();
	 	    Product product = findProduct.get();
	 	    order.setUser(user);
	 	    order.setOrderDate(LocalDateTime.now());
	 	    Address address = addressExists.get();
	 	    order.setShippingAddress(address.getFullAddress());
	 	    order.setOrderStatus(OrderStatus.PROCESSING);
	 	    order.setPaymentStatus(PaymentStatus.PAID);
	 	    order.setTotalPrice(orderDetails.getQuantity()*product.getProductPrice());
	 	    cartItem.setProductQuantity(cartItem.getProductQuantity()-orderDetails.getQuantity());
	 	    cartItem.setTotalPrice(cartItem.getProductQuantity()*product.getProductPrice());
	 	    product.setProductQuantity(product.getProductQuantity()-orderDetails.getQuantity());
	 	   for (CartItem item : productList) {
	 		    if (!item.getUser().getUserId().equals(orderDetails.getUserId())) {
	 		        int updatedQty = item.getProductQuantity() - orderDetails.getQuantity();
	 		        if (updatedQty <= 0) {
	 		            cartItemRepo.delete(item);
	 		        } else {
	 		            item.setProductQuantity(updatedQty);
	 		            item.setTotalPrice(updatedQty * product.getProductPrice());
	 		            cartItemRepo.save(item);
	 		        }
	 		    }
	 		}
	 	    orderItem.setOrder(order);
	 	    orderItem.setProduct(product);
	 	    orderItem.setQuantity(orderDetails.getQuantity());
	 	    
	 	    ArrayList<OrderItem> items = new ArrayList<>();
	 	    items.add(orderItem);
	 	    order.setItems(items);
	 	    orderRepo.save(order); 
	 	    
	    }
	    ApiResponse<OrderProduct> response = new ApiResponse<>();
	    response.setData(order);
	    response.setMessage("Order Placed Successfully");
	    return ResponseEntity.ok(response);
	
	}

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderByUser(Long userId) {
		
		List<OrderProduct> orders = orderRepo.findByUser(userId);
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(currUser.getUserId()!= userId ) {
			throw new UnAuthorizedException("Not Authorized to See Another Users Order Details");
		}

		boolean isSelf = currUser.getUserId().equals(userId);
		boolean isManager = currUser.getUserPermissions().contains(AdminPermissions.Manager);
		boolean isOrderManager = currUser.getUserPermissions().contains(AdminPermissions.Order_Manager);
		
		if (!isSelf && !(isManager || isOrderManager)) {
		    throw new UnAuthorizedException("Not Authorized to View This User's Order Details");
		}
		
		if (currUser.getUserRole() == Role.ADMIN && !(isManager || isOrderManager)) {
		    throw new UnAuthorizedException("You don't have rights to view order details");
		}
		if(orders.isEmpty()) {
			throw new UserNotFoundException("No Order Details Found with Given User ID");
		}
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orders);
		response.setMessage("User "+userId+" Orders Details");
		return ResponseEntity.ok(response);
	}

	@Transactional
	public ResponseEntity<ApiResponse<OrderProduct>> cancelOrder(Long userId, Long productId, int quantity) {

	    User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(currUser.getUserId()!= userId) {
			throw new UnAuthorizedException("Not Authorized to Cancel Order With Another Account");
		}
		
	    Optional<OrderProduct> orderExists = orderRepo.findByUserAndProductAndQuantity(userId, productId, quantity);
	    if (!orderExists.isPresent()) {
	        throw new ProductNotFoundException("No matching order found for user " + userId + " with product " 
	        								+ productId + " and quantity " + quantity);
	    }
	    OrderProduct order = orderExists.get();

	    OrderItem items = null;
	    for (OrderItem item : order.getItems()) {
	        if (item.getProduct().getProductId().equals(productId) && item.getQuantity() == quantity) {
	            items = item;
	            break;
	        }
	    }

	    if (items == null) {
	        throw new ProductNotFoundException("No matching item found in order for product " + productId);
	    }
	    
	    Optional<Product> productExists = productRepo.findById(productId);
//	    if (!productExists.isPresent()) {
//	        throw new ProductNotFoundException("Product " + productId + " not found");
//	    }

	    Product product = productExists.get();
	    product.setProductQuantity(product.getProductQuantity() + items.getQuantity());
	    productRepo.save(product);
	    orderRepo.delete(order);

	    ApiResponse<OrderProduct> response = new ApiResponse<>();
	    response.setMessage("Order cancelled successfully");
	    return ResponseEntity.ok(response);
	}


	public ResponseEntity<ApiResponse<List<OrderProduct>>> getByUserIdAndProductId(Long userId, Long productId) {
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		boolean isSelf = currUser.getUserId().equals(userId);
		boolean isManager = currUser.getUserPermissions().contains(AdminPermissions.Manager);
		boolean isOrderManager = currUser.getUserPermissions().contains(AdminPermissions.Order_Manager);
		
		if (!isSelf && !(isManager || isOrderManager)) {
		    throw new UnAuthorizedException("Not Authorized to View This User's Order Details");
		}
		
		if (currUser.getUserRole() == Role.ADMIN && !(isManager || isOrderManager)) {
		    throw new UnAuthorizedException("Only Manager and Order Manager have rights to view order details");
		}
		List<OrderProduct> orders = orderRepo.findAllByUserAndProduct(userId, productId);
		
		if(orders.isEmpty()) {
			throw new ProductNotFoundException("Orders Not Found With This UserID "+userId+" and ProductID "+productId);
		}
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orders);
		response.setMessage("Orders Details");
		return ResponseEntity.ok(response);
	}
	
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getAllOrders() {
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		boolean isManager = currUser.getUserPermissions().contains(AdminPermissions.Manager);
		boolean isOrderManager = currUser.getUserPermissions().contains(AdminPermissions.Order_Manager);
		
		if (currUser.getUserRole() != Role.ADMIN) {
		    throw new UnAuthorizedException("User Not Authorized to View This All Order Details");
		}
		
		if (currUser.getUserRole() == Role.ADMIN && !(isManager || isOrderManager)) {
		    throw new UnAuthorizedException("Only Manager and Order Manager have rights to view All order details");
		}
		List<OrderProduct> orderList = orderRepo.findAll();
		
		if(orderList.isEmpty()) {
			throw new CustomException("No Order Found");
		}
		
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orderList);
		response.setMessage("All Orders Details");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderStatus(OrderStatus status) {
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
		if(currUser.getUserRole()!= Role.ADMIN) {
			throw new UnAuthorizedException("User Not Allowed to See Order Statuses");
		}

		boolean isManager = currUser.getUserPermissions().contains(AdminPermissions.Manager);
		boolean isOrderManager = currUser.getUserPermissions().contains(AdminPermissions.Order_Manager);

		if (currUser.getUserRole() == Role.ADMIN && !(isManager || isOrderManager)) {
		    throw new UnAuthorizedException("Only Manager and Order Manager have rights to view order Statuses");
		}
		List<OrderProduct> orders = orderRepo.findAllByOrderStatus(status);
		if(orders.isEmpty()) {
			throw new CustomException("No Order Found with Order Status "+status);
		}
		
		
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orders);
		response.setMessage("Order Details with Order Status "+status);
		return ResponseEntity.ok(response);
	}
	
	public ResponseEntity<ApiResponse<List<OrderProduct>>> getOrderByPayment(PaymentStatus paymentStatus) {
		
		User currUser = currentUser.getUser();
		if(currUser == null) {
			throw new UnAuthorizedException("Please Login");
		}
 		if(currUser.getUserRole()!= Role.ADMIN) {
			throw new UnAuthorizedException("User Not Allowed to See Payment Statuses");
		}
 		boolean isManager = currUser.getUserPermissions().contains(AdminPermissions.Manager);
		boolean isOrderManager = currUser.getUserPermissions().contains(AdminPermissions.Order_Manager);

		if (currUser.getUserRole() == Role.ADMIN && !(isManager || isOrderManager)) {
		    throw new UnAuthorizedException("You don't have rights to update user roles");
		}
		List<OrderProduct> orders = orderRepo.findAllByPaymentStatus(paymentStatus);
		if(orders.isEmpty()) {
			throw new CustomException("No Orders Found With Payment Status "+paymentStatus);
		}
		ApiResponse<List<OrderProduct>> response = new ApiResponse<>();
		response.setData(orders);
		response.setMessage("Order Details with Payment Status "+paymentStatus);
		return ResponseEntity.ok(response);
	}

}