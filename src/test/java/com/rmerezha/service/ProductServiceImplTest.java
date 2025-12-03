package com.rmerezha.service;

import com.rmerezha.domain.Product;
import com.rmerezha.domain.Category;
import com.rmerezha.exception.ProductAlreadyExistsException;
import com.rmerezha.exception.ProductNotFoundException;
import com.rmerezha.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ProductServiceImpl.class)
@Tag("service")
class ProductServiceImplTest {

    @MockitoBean
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("findAllProducts: should return a list of all products")
    void testFindAllProducts_WhenCalled_ReturnsList() {
        Product p1 = new Product(1L, "Milk", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);
        Product p2 = new Product(2L, "Yarn", "Desc", BigDecimal.ONE, 5, "Mars", Category.TOY);

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Product> result = productService.findAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(p1, result.get(0));
        assertEquals(p2, result.get(1));
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findProductById: should return product when exists")
    void testFindProductById_WhenExists_ReturnsProduct() {
        Long id = 1L;
        String name = "Milk";
        Product product = new Product(id, name, "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        Product result = productService.findProductById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
    }

    @Test
    @DisplayName("findProductById: should throw exception when product not found")
    void testFindProductById_WhenNotFound_ThrowsException() {
        Long id = 999L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findProductById(id));
        verify(productRepository).findById(id);
    }

    @Test
    @DisplayName("createProduct: should create product when name is unique")
    void testCreateProduct_WhenNameUnique_ReturnsCreatedProduct() {
        Product newProduct = new Product(null, "Unique Name", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);
        Product savedProduct = new Product(1L, "Unique Name", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);

        when(productRepository.existsByName(newProduct.getName())).thenReturn(false);
        when(productRepository.save(newProduct)).thenReturn(savedProduct);

        Product result = productService.createProduct(newProduct);

        assertNotNull(result.getId());
        assertEquals("Unique Name", result.getName());
        verify(productRepository).save(newProduct);
    }

    @Test
    @DisplayName("createProduct: should throw exception when name is duplicate")
    void testCreateProduct_WhenNameDuplicate_ThrowsException() {
        Product newProduct = new Product(null, "Duplicate Name", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);

        when(productRepository.existsByName(newProduct.getName())).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class, () -> productService.createProduct(newProduct));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProduct: should update product when valid")
    void testUpdateProduct_WhenValid_ReturnsUpdatedProduct() {
        Long id = 1L;
        Product existingProduct = new Product(id, "Old Name", "Old Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);
        Product changes = new Product();
        changes.setName("New Name");
        changes.setPrice(BigDecimal.valueOf(99.99));

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByName("New Name")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.updateProduct(id, changes);

        assertEquals("New Name", result.getName());
        assertEquals(BigDecimal.valueOf(99.99), result.getPrice());
        assertEquals("Old Desc", result.getDescription());

        verify(productRepository).save(existingProduct);
    }

    @Test
    @DisplayName("updateProduct: should throw exception when name conflict occurs")
    void testUpdateProduct_WhenNameConflict_ThrowsException() {
        Long id = 1L;
        Product existingProduct = new Product(id, "Old Name", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);
        Product changes = new Product();
        changes.setName("Taken Name");

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByName("Taken Name")).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class, () -> productService.updateProduct(id, changes));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProduct: should update ALL fields when all are provided")
    void testUpdateProduct_AllFields_UpdatesEverything() {

        Long id = 1L;
        Product existingProduct = new Product(id, "Old Name", "Old Desc", BigDecimal.ONE, 0, "Earth", Category.FOOD);

        Product changes = new Product();
        changes.setName("New Name");
        changes.setDescription("New Desc");
        changes.setPrice(BigDecimal.TEN);
        changes.setStockQuantity(100);
        changes.setOriginPlanet("Mars");
        changes.setCategory(Category.TOY);

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByName("New Name")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));


        Product result = productService.updateProduct(id, changes);

        assertAll("Check all fields updated",
                () -> assertEquals("New Name", result.getName()),
                () -> assertEquals("New Desc", result.getDescription()),
                () -> assertEquals(BigDecimal.TEN, result.getPrice()),
                () -> assertEquals(100, result.getStockQuantity()),
                () -> assertEquals("Mars", result.getOriginPlanet()),
                () -> assertEquals(Category.TOY, result.getCategory())
        );

        verify(productRepository).save(existingProduct);
    }

    @Test
    @DisplayName("updateProduct: should skip logic for null fields (partial update)")
    void testUpdateProduct_NullFields_Ignored() {
        Long id = 1L;
        Product existingProduct = new Product(id, "Old Name", "Old Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);

        Product changes = new Product();
        changes.setPrice(BigDecimal.valueOf(50));

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.updateProduct(id, changes);

        assertEquals(BigDecimal.valueOf(50), result.getPrice());
        assertEquals("Old Name", result.getName());

        verify(productRepository, never()).existsByName(anyString());
    }

    @Test
    @DisplayName("updateProduct: should skip validation when name is same (case-insensitive) and skip null fields")
    void testUpdateProduct_WhenNameSame_And_PriceNull() {
        Long id = 1L;
        Product existingProduct = new Product(id, "Milk", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);

        Product changes = new Product();
        changes.setName("milk");
        changes.setDescription("Updated Desc");

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.updateProduct(id, changes);

        assertEquals("milk", result.getName());
        assertEquals(BigDecimal.TEN, result.getPrice());
        assertEquals("Updated Desc", result.getDescription());

        verify(productRepository, never()).existsByName(anyString());
        verify(productRepository).save(existingProduct);
    }

    @Test
    @DisplayName("deleteProductById: should verify repository delete call")
    void testDeleteProductById_WhenCalled_VerifiesRepositoryCall() {
        Long id = 1L;

        productService.deleteProductById(id);

        verify(productRepository, times(1)).deleteById(id);
    }
}