package com.example.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.customannotations.ForProduct;
import com.example.enums.AdminPermissions;
import com.example.enums.Role;
import com.example.model.Product;
import com.example.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

	
	private ProductService productService;
	
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("/add-product")
	@ForProduct(validPermissions = { AdminPermissions.Manager, AdminPermissions.Product_Manager},requiredRole = Role.ADMIN)
	public ResponseEntity<ApiResponse<Product>> addProduct(@RequestBody Product product) {		
		return productService.saveProduct(product);
	}
	
	@PutMapping("/update/{productId}")
	@ForProduct(validPermissions = {AdminPermissions.Manager,AdminPermissions.Product_Manager},requiredRole = Role.ADMIN)
	public ResponseEntity<ApiResponse<Product>> updateProductById(@PathVariable Long productId, @RequestBody Product product) {
		return productService.productUpdate(productId, product);
	}
	
	@GetMapping("/get-product-by-id/{productId}")
	public ResponseEntity<ApiResponse<Product>> getById(@PathVariable Long productId) {		
		return productService.getProductById(productId);
	}
	
	@DeleteMapping("/delete-by-id/{productId}")
	@ForProduct(validPermissions = {AdminPermissions.Manager, AdminPermissions.Product_Manager},requiredRole = Role.ADMIN)
	public ResponseEntity<ApiResponse<Product>> deleteById(@PathVariable Long productId) {
		return productService.deleteById(productId);
	}
	
	@GetMapping("/get-product-by-category/{category}")
	public ResponseEntity<ApiResponse<List<Product>>> getProductByCategory(@PathVariable String category) {
		return productService.getProductByCategory(category);
	}
	
	@GetMapping("/getall")
	public ResponseEntity<ApiResponse<List<Product>>> displayAllProducts() {	 
		return productService.displayAllProducts();
	}
	
	@GetMapping("/get-product-by-range/{category}/{minPrice}/{maxPrice}")
	public ResponseEntity<ApiResponse<List<Product>>> getProductByPrice(@PathVariable String category, @PathVariable double minPrice, @PathVariable double maxPrice) {
		return productService.getProductBetweenPrice(category, minPrice, maxPrice);
	}
}

/* Grid container for filtered products */
//.product-grid {
//  display: grid;
//  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
//  gap: 1.5rem;
//  padding: 2rem;
//  background-color: #f5f5f5;
//}
//
///* Individual product card */
//.product-card {
//  background-color: #ffffff;
//  border-radius: 10px;
//  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
//  overflow: hidden;
//  transition: transform 0.3s ease, box-shadow 0.3s ease;
//  padding: 1rem;
//  text-align: center;
//}
//
///* Hover effect */
//.product-card:hover {
//  transform: translateY(-5px);
//  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
//}
//
///* Product image */
//.product-card img {
//  width: 100%;
//  height: 200px;
//  object-fit: cover;
//  border-radius: 8px;
//  margin-bottom: 1rem;
//}
//
///* Product name */
//.product-card h3 {
//  font-size: 1.2rem;
//  color: #333;
//  margin: 0.5rem 0;
//}
//
///* Product description */
//.product-card p {
//  font-size: 0.95rem;
//  color: #666;
//  margin: 0.5rem 0;
//}
//
///* Product price */
//.product-card span {
//  display: block;
//  font-size: 1rem;
//  font-weight: bold;
//  color: #3f51b5;
//  margin: 0.5rem 0;
//}
//
///* Product category */
//.product-card small {
//  font-size: 0.8rem;
//  color: #999;
//}
//
///* No results message */
//p {
//  text-align: center;
//  font-size: 1rem;
//  color: #888;
//  margin-top: 2rem;
//}
//
///* Responsive tweaks */
//@media (max-width: 600px) {
//  .product-grid {
//    grid-template-columns: 1fr;
//    padding: 1rem;
//  }
//
//  .product-card img {
//    height: 180px;
//  }
//}
