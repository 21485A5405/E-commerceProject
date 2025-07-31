package com.example.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.controller.ApiResponse;
import com.example.model.Product;

public interface ProductService {
	
	public ResponseEntity<ApiResponse<Product>> saveProduct(Product product);
	
	public ResponseEntity<ApiResponse<Product>> productUpdate(Long productId, Product product);
	
	public ResponseEntity<ApiResponse<Product>> deleteById(Long productId);
	
	public ResponseEntity<ApiResponse<Product>> getProductById(Long productId);
	
	public ResponseEntity<ApiResponse<List<Product>>> getProductByCategory(String category);

	public ResponseEntity<ApiResponse<List<Product>>> displayAllProducts();

	public ResponseEntity<ApiResponse<List<Product>>> getProductBetweenPrice(String category, double minPrice, double maxPrice);

	public ResponseEntity<String> updateQuantity(Long productId, int quantity);

}
