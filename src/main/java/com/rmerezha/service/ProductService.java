package com.rmerezha.service;

import com.rmerezha.domain.Product;
import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    Product findProductById(Long id);
    List<Product> findAllProducts();
    Product updateProduct(Long id, Product product);
    void deleteProductById(Long id);
}
