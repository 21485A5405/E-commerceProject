package com.example.service;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.controller.ApiResponse;
import com.example.exception.CustomException;
import com.example.exception.ProductNotFoundException;
import com.example.model.Product;
import com.example.repo.CartItemRepo;
import com.example.repo.OrderItemRepo;
import com.example.repo.OrderRepo;
import com.example.repo.ProductRepo;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService{
	

	private ProductRepo productRepo;
	private CartItemRepo cartItemRepo;
	private OrderRepo orderRepo;
	private OrderItemRepo orderItemRepo;
	
	
	public ProductServiceImpl(ProductRepo productRepo, OrderItemRepo orderItemRepo, CartItemRepo cartItemRepo, OrderRepo orderRepo) {
		this.productRepo = productRepo;
		this.cartItemRepo = cartItemRepo;
		this.orderRepo = orderRepo;
		this.orderItemRepo = orderItemRepo;
	}

	public ResponseEntity<ApiResponse<Product>> saveProduct(Product product) {
		
		if(product == null) {
			throw new ProductNotFoundException("Product Cannot be Empty");
		}else if(product.getProductName() == null || product.getProductName().isBlank()) {
			throw new ProductNotFoundException("Product Name Cannot be Null");
		}else if(product.getProductCategory() == null || product.getProductCategory().isBlank()) {
			throw new ProductNotFoundException("Product Category Cannot Null");
		}else if(product.getProductImageURL() == null  || product.getProductImageURL().isBlank()) {
			throw new ProductNotFoundException("Product ImageURL Cannot be Null");
		}else if(product.getProductPrice() <= 0.0) {
			throw new ProductNotFoundException("Product Price Canot be Less than Zero");
		}else if(product.getProductDescription() == null || product.getProductDescription().isBlank()) {
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

	@Transactional
	public ResponseEntity<ApiResponse<Product>> productUpdate(Long productId, Product newProduct) {
		
		Optional<Product> exists= productRepo.findById(productId);
		
		if(!exists.isPresent()) {
			throw new ProductNotFoundException("Product Not Found");
		}
		
			Product product = exists.get();
			product.setProductName(newProduct.getProductName());
			product.setProductPrice(newProduct.getProductPrice());
			product.setProductQuantity(newProduct.getProductQuantity());
			product.setProductCategory(newProduct.getProductCategory());
			product.setProductImageURL(newProduct.getProductImageURL());
			
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

	@Transactional
	public ResponseEntity<ApiResponse<Product>> deleteById(Long productId) {
		
		Optional<Product> exists = productRepo.findById(productId);
		if(!exists.isPresent()) {
			throw new ProductNotFoundException("Product Not Found");
			
		}
			cartItemRepo.deleteById(productId);
			orderRepo.deleteById(productId);
			orderItemRepo.deleteAllByProductId(productId);
			productRepo.deleteById(productId);
			ApiResponse<Product> response = new ApiResponse<>();
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

	@Override
	public ResponseEntity<String> updateQuantity(Long productId, int quantity) {
		Optional<Product> exists = productRepo.findById(productId);
		if(!exists.isPresent()) {
			throw new ProductNotFoundException("No Product Found");
		}
		exists.get().setProductQuantity(quantity);
		productRepo.save(exists.get());
		return ResponseEntity.ok("Quantity Updated Successfully");
	}

}
