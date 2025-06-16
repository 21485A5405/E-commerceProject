package com.example.service;

import java.util.List;
import java.util.Optional;

import com.example.model.Product;

public interface ProductService {
	
	public Product saveProduct(Product product);
	
	public String productUpdate(Long productId, Product product);
	
	public String deleteById(Long productId);
	
	public Optional<Product> getProductById(Long productId);
	
	public List<Product> getProductByCategory(String category);

	public List<Product> displayAllProducts(Product product);

	public List<Product> getProductBetweenPrice(String category, double minPrice, double maxPrice);

}
