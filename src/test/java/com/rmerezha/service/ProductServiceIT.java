package com.rmerezha.service;

import com.rmerezha.PostgresIT;
import com.rmerezha.domain.Category;
import com.rmerezha.domain.Product;
import com.rmerezha.exception.ProductAlreadyExistsException;
import com.rmerezha.exception.ProductNotFoundException;
import com.rmerezha.persistence.entity.ProductEntity;
import com.rmerezha.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class ProductServiceIT extends PostgresIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("createProduct: should create product when product is valid")
    void testCreateProduct_WhenProductIsValid_ReturnsCreatedProduct() {
        Product newProduct = createDummyProduct("Space Juice");

        Product created = productService.createProduct(newProduct);

        assertNotNull(created.getId());
        assertEquals("Space Juice", created.getName());
        assertEquals(1, productRepository.count());
    }

    @Test
    @DisplayName("createProduct: should throw exception when creating duplicate product")
    void testCreateProduct_WhenNameDuplicate_ThrowsException() {
        Product product = createDummyProduct("Duplicate Name");
        productService.createProduct(product);

        assertThrows(ProductAlreadyExistsException.class, () -> {
            productService.createProduct(createDummyProduct("Duplicate Name"));
        });
    }

    @Test
    @DisplayName("findProductById: should return product when exists")
    void testFindProductById_WhenExists_ReturnsProduct() {
        Product product = productService.createProduct(createDummyProduct("Find Me"));

        Product found = productService.findProductById(product.getId());

        assertNotNull(found);
        assertEquals("Find Me", found.getName());
        assertEquals(product.getId(), found.getId());
    }

    @Test
    @DisplayName("findProductById: should throw exception when product not found")
    void testFindProductById_WhenNotFound_ThrowsException() {
        assertThrows(ProductNotFoundException.class, () -> {
            productService.findProductById(999L);
        });
    }

    @Test
    @DisplayName("findAllProducts: should return a list of all products")
    void testFindAllProducts_WhenCalled_ReturnsList() {
        productService.createProduct(createDummyProduct("Product 1"));
        productService.createProduct(createDummyProduct("Product 2"));

        List<Product> products = productService.findAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
    }

    @Test
    @DisplayName("updateProduct: should update product when valid")
    void testUpdateProduct_WhenValid_ReturnsUpdatedProduct() {
        Product original = productService.createProduct(createDummyProduct("Original Name"));

        Product changes = new Product();
        changes.setName("Updated Name");
        changes.setPrice(BigDecimal.valueOf(999.99));

        Product updated = productService.updateProduct(original.getId(), changes);

        assertEquals("Updated Name", updated.getName());
        assertEquals(0, BigDecimal.valueOf(999.99).compareTo(updated.getPrice()));

        ProductEntity inDb = productRepository.findById(original.getId()).orElseThrow();
        assertEquals("Updated Name", inDb.getName());
    }

    @Test
    @DisplayName("deleteProductById: should delete product from database")
    void testDeleteProductById_WhenCalled_DeletesEntity() {
        Product product = productService.createProduct(createDummyProduct("Delete Me"));

        productService.deleteProductById(product.getId());

        assertFalse(productRepository.existsById(product.getId()));
    }

    private Product createDummyProduct(String name) {
        Product product = new Product();
        product.setName(name);
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(100.00));
        product.setStockQuantity(10);
        product.setOriginPlanet("Earth");
        product.setCategory(Category.FOOD);
        return product;
    }
}