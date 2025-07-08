package com.example.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long>{

	List<Product> findByProductCategory(String category);
	
	List<Product> findAll();
	
	@Query("SELECT p FROM Product p WHERE p.productCategory = :category AND p.productPrice BETWEEN :minPrice AND :maxPrice")
	List<Product> findProductsByPriceRange(
	    @Param("category") String category,
	    @Param("minPrice") double minPrice,
	    @Param("maxPrice") double maxPrice);

	@Query("SELECT p.productId FROM Product p")
	List<Long> getAllProductIds();
}