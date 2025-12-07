package com.rmerezha.service;

import com.rmerezha.domain.Product;
import com.rmerezha.exception.ProductAlreadyExistsException;
import com.rmerezha.exception.ProductNotFoundException;
import com.rmerezha.mapper.ProductMapper;
import com.rmerezha.persistence.entity.ProductEntity;
import com.rmerezha.persistence.mapper.ProductEntityMapper;
import com.rmerezha.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductEntityMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        var entities = productRepository.findAll();
        return productMapper.toDomainList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public Product findProductById(Long id) {
        var entity = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.existsByNameIgnoreCase(product.getName())) {
            throw new ProductAlreadyExistsException(product.getName());
        }
        var entity = productMapper.toEntity(product);
        var created = productRepository.save(entity);
        return productMapper.toDomain(created);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product productChanges) {
        Product existingProduct = findProductById(id);

        if (productChanges.getName() != null &&
                !productChanges.getName().equalsIgnoreCase(existingProduct.getName())) {

            if (productRepository.existsByNameIgnoreCase(productChanges.getName())) {
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

        var entity = productRepository.save(productMapper.toEntity(existingProduct));
        return productMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
}