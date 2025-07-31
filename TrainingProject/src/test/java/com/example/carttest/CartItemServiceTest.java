package com.example.carttest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import com.example.authentication.CurrentUser;
import com.example.controller.ApiResponse;
import com.example.exception.CustomException;
import com.example.exception.ProductNotFoundException;
import com.example.exception.UnAuthorizedException;
import com.example.model.CartItem;
import com.example.model.Product;
import com.example.model.User;
import com.example.repo.CartItemRepo;
import com.example.repo.ProductRepo;
import com.example.repo.UserRepo;
import com.example.service.CartItemServiceImpl;

public class CartItemServiceTest {

	@Mock private CartItemRepo cartItemRepo;
	@Mock private ProductRepo productRepo;
	@Mock private UserRepo userRepo;
	@Mock private CurrentUser currentUser;
	
	@InjectMocks CartItemServiceImpl cartItemService;
	
	@Test
	void testAddProductToCart() {
	    User user = new User();
	    user.setUserId(1L);

	    Product product = new Product();
	    product.setProductId(100L);
	    product.setProductQuantity(10);
	    product.setProductPrice(50);

	    CartItem cartItem = new CartItem();
	    cartItem.setUser(user);
	    cartItem.setProduct(product);
	    cartItem.setProductQuantity(2);
	    cartItem.setTotalPrice(100);

	    when(currentUser.getUser()).thenReturn(user);
	    when(userRepo.findById(1L)).thenReturn(Optional.of(user));
	    when(productRepo.findById(100L)).thenReturn(Optional.of(product));
	    when(cartItemRepo.findByUserAndProduct(1L, 100L)).thenReturn(Optional.empty());
	    when(cartItemRepo.save(any(CartItem.class))).thenReturn(cartItem);

	    ResponseEntity<ApiResponse<CartItem>> response = cartItemService.addProductToCart(1L, 100L);

	    assertEquals("Item Added Into Cart Successfully", response.getBody().getMessage());
	    assertEquals(100, response.getBody().getData().getTotalPrice());
	}
	
	@Test
	void testLoginOrNot() {
	    when(currentUser.getUser()).thenReturn(null);

	    UnAuthorizedException exception = assertThrows(UnAuthorizedException.class, () -> cartItemService.addProductToCart(1L, 100L));
	    assertEquals("Please Login", exception.getMessage());
	}

	@Test
	void testProductNotFound() {
	    User user = new User();
	    user.setUserId(1L);

	    when(currentUser.getUser()).thenReturn(user);
	    when(userRepo.findById(1L)).thenReturn(Optional.of(user));
	    when(productRepo.findById(100L)).thenReturn(Optional.empty());

	    ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> cartItemService.addProductToCart(1L, 100L));
	    assertEquals("Product Not Found to Add Into Cart", exception.getMessage());
	}

	@Test
	void testAddProductToCart_WhenQuantityExceedsStock_ShouldThrowCustomException() {
	    User user = new User();
	    user.setUserId(1L);

	    Product product = new Product();
	    product.setProductId(100L);
	    product.setProductQuantity(5); // Only 5 in stock

	    when(currentUser.getUser()).thenReturn(user);
	    when(userRepo.findById(1L)).thenReturn(Optional.of(user));
	    when(productRepo.findById(100L)).thenReturn(Optional.of(product));
	    when(cartItemRepo.findByUserAndProduct(1L, 100L)).thenReturn(Optional.empty());

	    CustomException exception = assertThrows(CustomException.class, () -> cartItemService.addProductToCart(1L, 100L));
	    assertTrue(exception.getMessage().contains("Enough Quantity Selected"));
	}

	@Test
	void testAddToAnotherAccount() {
	    User current = new User();
	    current.setUserId(2L); // Logged-in user

	    User target = new User();
	    target.setUserId(1L); // Target user

	    Product product = new Product();
	    product.setProductId(100L);
	    product.setProductQuantity(10);

	    when(currentUser.getUser()).thenReturn(current);
	    when(userRepo.findById(1L)).thenReturn(Optional.of(target));
	    when(productRepo.findById(100L)).thenReturn(Optional.of(product));

	    UnAuthorizedException exception = assertThrows(UnAuthorizedException.class, () -> cartItemService.addProductToCart(1L, 100L));
	    assertEquals("User Not Authorized to Add Product Into Another Account", exception.getMessage());
	}


}
