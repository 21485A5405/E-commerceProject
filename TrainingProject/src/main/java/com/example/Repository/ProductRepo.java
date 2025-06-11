package com.example.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Entity.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long>{

}
