package com.example.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Entity.Product;
import com.example.Service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

	
	@Autowired
	private ProductService productService;

	@PostMapping("/addproduct")
	public Product addProduct(@RequestBody Product product) {
		
		return productService.saveProduct(product);
	}
	
	@PutMapping("/update/{productId}")
	public ResponseEntity<String> updateProductById(@PathVariable Long productId, @RequestBody Product product) {
		
		String message = productService.productUpdate(productId, product);
		return ResponseEntity.ok(message);
		
	}
	
	@GetMapping("/getproductbyid/{productId}")
	public Optional<Product> getById(@PathVariable Long productId) {
		
		return productService.getProductById(productId);
	}
	
	@DeleteMapping("/deletebyid/{productId}")
	public String deleteById(@PathVariable Long productId) {
		
		String message = productService.deleteById(productId);
		return message;
	}
	
	@GetMapping("/getproductbycategory/{category}")
	public List<Product> getProductByCategory(@PathVariable String category) {
		
		return productService.getProductByCategory(category);
	}
	
	@GetMapping("/getallproducts")
	public List<Product> displayAllProducts(Product product) {
		
		return productService.displayAllProducts(product);
	}
	
		
}
