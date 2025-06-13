package com.example.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import com.example.Entity.Product;
import com.example.Repository.ProductRepo;
import com.example.exceptionfile.ProductNotFoundException;
@Service
public class ProductService {
	
	@Autowired
	private ProductRepo productRepo;

	public Product saveProduct(Product product) {
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
			p.setProductCategory(product.getProductCategory());
			productRepo.save(p);
		}
		
		return "Product Updated Successfully";
	}

	public Optional<Product> getProductById(Long productId) {
		
		return productRepo.findById(productId);
	}


	public String deleteById(Long productId) {
		// TODO Auto-generated method stub
		
		Optional<Product> p = productRepo.findById(productId);
		if(!p.isPresent()) {
			throw new ProductNotFoundException("Product Not Found");
		}else {
			
			productRepo.deleteById(productId);
		}
		return "Product Deleted Successfully";
	}


	public List<Product> getProductByCategory(String category) {
		// TODO Auto-generated method stub
		List<Product> p = productRepo.findByProductCategory(category);
		
		if(p.isEmpty()) {
			throw new ProductNotFoundException("No Product Found Under that Category");
		}
		return p;
	}


	public List<Product> displayAllProducts(Product product) {
		// TODO Auto-generated method stub
		
		return productRepo.findAll();
	}

}
