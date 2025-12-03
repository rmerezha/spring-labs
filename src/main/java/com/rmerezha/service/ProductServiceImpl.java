package com.rmerezha.service;

import com.rmerezha.domain.Product;
import com.rmerezha.exception.ProductAlreadyExistsException;
import com.rmerezha.exception.ProductNotFoundException;
import com.rmerezha.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public Product createProduct(Product product) {
        if (productRepository.existsByName(product.getName())) {
            throw new ProductAlreadyExistsException(product.getName());
        }
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product productChanges) {
        Product existingProduct = findProductById(id);

        if (productChanges.getName() != null &&
                !productChanges.getName().equalsIgnoreCase(existingProduct.getName())) {

            if (productRepository.existsByName(productChanges.getName())) {
                throw new ProductAlreadyExistsException(productChanges.getName());
            }
        }

        if (productChanges.getName() != null) existingProduct.setName(productChanges.getName());
        if (productChanges.getDescription() != null) existingProduct.setDescription(productChanges.getDescription());
        if (productChanges.getPrice() != null) existingProduct.setPrice(productChanges.getPrice());
        if (productChanges.getStockQuantity() != null)
            existingProduct.setStockQuantity(productChanges.getStockQuantity());
        if (productChanges.getOriginPlanet() != null) existingProduct.setOriginPlanet(productChanges.getOriginPlanet());
        if (productChanges.getCategory() != null) existingProduct.setCategory(productChanges.getCategory());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
}