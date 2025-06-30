package com.example.service;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.authentication.CurrentUser;
import com.example.controller.ApiResponse;
import com.example.exception.CustomException;
import com.example.exception.ProductNotFoundException;
import com.example.exception.UnAuthorizedException;
import com.example.exception.UserNotFoundException;
import com.example.model.Product;
import com.example.model.Role;
import com.example.model.User;
import com.example.repo.ProductRepo;
import com.example.repo.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService{
	

	private ProductRepo productRepo;
	private UserRepo userRepo;
	private CurrentUser currentUser;
	
	public ProductServiceImpl(ProductRepo productRepo, UserRepo userRepo, CurrentUser currentUser) {
		this.productRepo = productRepo;
		this.userRepo = userRepo;
		this.currentUser = currentUser;
	}

	public ResponseEntity<ApiResponse<Product>> saveProduct(Product product, Long userId) {
				
		Optional<User> user = userRepo.findById(userId);
		
		if(!user.isPresent()) {
			
			throw new UserNotFoundException("User Not Found");
		}
		if (!user.get().getUserRole().equals(Role.ADMIN)) {
		    throw new UnAuthorizedException("You Don't Have Authorization To Add Product");
		}

		if(product == null) {
			throw new ProductNotFoundException("Product Cannot be Empty");
		}else if(product.getProductName() == null) {
			throw new ProductNotFoundException("Product Name Cannot be Null");
		}else if(product.getProductCategory() == null ) {
			throw new ProductNotFoundException("Product Category Cannot Null");
		}else if(product.getProductImageURL() == null) {
			throw new ProductNotFoundException("Product ImageURL Cannot be Null");
		}else if(product.getProductPrice() <= 0.0) {
			throw new ProductNotFoundException("Product Price Canot be Less than Zero");
		}else if(product.getProductDescription() == null) {
			throw new ProductNotFoundException("Product Description Cannot be Empty");
		}else if(product.getProductQuantity() <= 0) {
			throw new ProductNotFoundException("Product Quantity cannot be less than 0");
		}
		productRepo.save(product);
		ApiResponse<Product> response = new ApiResponse<>();
		response.setData(product);
		response.setMessage("New Product Added Successfully");
	
	return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<Product>> productUpdate(Long productId, Product newProduct, Long userId) {
		
		Optional<Product> exists= productRepo.findById(productId);
		
		if(!exists.isPresent()) {
			throw new ProductNotFoundException("Product Not Found");
		}
		
		Optional<User> user = userRepo.findById(userId);
		
		if(!user.isPresent() || user.get().getUserRole() !=Role.ADMIN) {
			throw new UnAuthorizedException("User Not Found / You Dont Have Authorization to Update Product");
			
		}
			Product product = exists.get();
			product.setProductName(newProduct.getProductName());
			product.setProductPrice(newProduct.getProductPrice());
			product.setProductQuantity(newProduct.getProductQuantity());
			product.setProductCategory(newProduct.getProductCategory());
			
			productRepo.save(product);
			ApiResponse<Product> response = new ApiResponse<>();
			response.setData(product);
			response.setMessage("Product Updated Successfully");
		
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<Product>> getProductById(Long productId) {
		
		Optional<Product> exists = productRepo.findById(productId);
		if(!exists.isPresent()) {
			throw new ProductNotFoundException("Product Not Found");
		}
		ApiResponse<Product> response = new ApiResponse<>();
		Product product = exists.get();
		response.setData(product);
		response.setMessage("Product "+productId+" Details");
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public ResponseEntity<ApiResponse<Product>> deleteById(Long productId, Long userId) {
		
		Optional<Product> exists = productRepo.findById(productId);
		if(!exists.isPresent()) {
			throw new ProductNotFoundException("Product Not Found");
			
		}
		Optional<User> user = userRepo.findById(userId);
		
		if(!user.isPresent()) {
			throw new UnAuthorizedException("User Not Found");
		}
		if(user.get().getUserRole() != Role.ADMIN) {
			
			throw new UnAuthorizedException("You Dont Have Authorization to Delete Product");
		}
			productRepo.deleteById(productId);
			Product product = exists.get();
			ApiResponse<Product> response = new ApiResponse<>();
			response.setData(product);
			response.setMessage("Product Deleted Successfully");
			return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<Product>>> getProductByCategory(String category) {
		
		List<Product> exists = productRepo.findByProductCategory(category);	
		if(exists.isEmpty()) {
			throw new ProductNotFoundException("No Product Found Under the Category "+category);
		}
		ApiResponse<List<Product>> response = new ApiResponse<>();
		response.setData(exists);
		response.setMessage("Product Details");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<Product>>> displayAllProducts() {	
		List<Product> products = productRepo.findAll();
		
		ApiResponse<List<Product>> response = new ApiResponse<>();
		response.setData(products);
		response.setMessage("All Product Details");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<ApiResponse<List<Product>>> getProductBetweenPrice(String category, double minPrice, double maxPrice) {
		
		List<Product> products = productRepo.findProductsByPriceRange(category, minPrice, maxPrice);
		
		if(products.isEmpty()) {
			throw new CustomException("No Items Found Between That PriceRange");
		}
		ApiResponse<List<Product>> response = new ApiResponse<>();
		response.setData(products);
		response.setMessage("Products Between "+minPrice+" And "+maxPrice);
		return ResponseEntity.ok(response);
	}

}
