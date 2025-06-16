package com.example.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long>{

	List<Product> findByProductCategory(String category);
	
	List<Product> findAll();
	
	List<Product> findByProductCategoryAndProductPriceBetween(String category, double minPrice, double maxPrice);

}