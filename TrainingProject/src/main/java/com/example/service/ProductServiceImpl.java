package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.exceptionfile.CustomException;
import com.example.exceptionfile.ProductNotFoundException;
import com.example.model.Product;
import com.example.repo.ProductRepo;
@Service
public class ProductServiceImpl implements ProductService{
	
	@Autowired
	private ProductRepo productRepo;

	public Product saveProduct(Product product) {
		
		if(product == null) {
			throw new ProductNotFoundException("Product Cannot be Empty");
			
		}else if(product.getProductName() == null) {
			
			throw new ProductNotFoundException("Product Name Cannot be Null");
			
		}else if(product.getProductCategory() == null ) {
			
			throw new ProductNotFoundException("Product Category Cannot Null");
			
		}else if(product.getProductImageURL() == null) {
			
			throw new ProductNotFoundException("Product ImageURL Cannot be Null");
			
		}else if(product.getProductPrice() <= 0.0) {
			
			throw new ProductNotFoundException("Product Price Canot Less than Zero");
			
		}else if(product.getProductDescription() == null) {
			
			throw new ProductNotFoundException("Product Description Cannot be Empty");
			
		}else if(product.getProductQuantity() <= 0) {
			
			throw new ProductNotFoundException("Product Quantity cannot be less than 0");
		}
		return productRepo.save(product);
	}

	public String productUpdate(Long productId, Product product) {
		// TODO Auto-generated method stub
		
		Optional<Product> exists= productRepo.findById(productId);
		
		if(!exists.isPresent()) {
			throw new ProductNotFoundException("Product Not Found");
		}else {
			
			Product p = exists.get();
			p.setProductName(product.getProductName());
			p.setProductPrice(product.getProductPrice());
			p.setProductQuantity(product.getProductQuantity());
			p.setProductCategory(product.getProductCategory());
			
			productRepo.save(p);
		}
		
		return "Product Updated Successfully";
	}

	public Optional<Product> getProductById(Long productId) {
		
		return productRepo.findById(productId);
	}


	public String deleteById(Long productId) {
		
		Optional<Product> p = productRepo.findById(productId);
		if(!p.isPresent()) {
			throw new ProductNotFoundException("Product Not Found");
			
		}else {
			productRepo.deleteById(productId);
		}
		return "Product Deleted Successfully";
	}


	public List<Product> getProductByCategory(String category) {
		
		List<Product> p = productRepo.findByProductCategory(category);
		
		if(p.isEmpty()) {
			throw new ProductNotFoundException("No Product Found Under that Category");
		}
		return p;
	}


	public List<Product> displayAllProducts(Product product) {
		
		return productRepo.findAll();
	}

	public List<Product> getProductBetweenPrice(String category, double minPrice, double maxPrice) {
		
		List<Product> p = productRepo.findByProductCategoryAndProductPriceBetween(category, minPrice, maxPrice);
		
		if(p.isEmpty()) {
			throw new CustomException("No Items Found Between That PriceRange");
		}
		return p;
	}

}
