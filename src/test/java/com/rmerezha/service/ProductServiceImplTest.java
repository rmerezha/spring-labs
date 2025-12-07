package com.rmerezha.service;

import com.rmerezha.domain.Category;
import com.rmerezha.domain.Product;
import com.rmerezha.exception.ProductAlreadyExistsException;
import com.rmerezha.exception.ProductNotFoundException;
import com.rmerezha.persistence.entity.ProductEntity;
import com.rmerezha.persistence.mapper.ProductEntityMapper;
import com.rmerezha.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @MockitoBean
    private ProductEntityMapper productMapper;

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("findAllProducts: should return a list of all products")
    void testFindAllProducts_WhenCalled_ReturnsList() {
        ProductEntity entity1 = new ProductEntity();
        entity1.setId(1L);
        entity1.setName("Milk");

        ProductEntity entity2 = new ProductEntity();
        entity2.setId(2L);
        entity2.setName("Yarn");

        Product p1 = new Product(1L, "Milk", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);
        Product p2 = new Product(2L, "Yarn", "Desc", BigDecimal.ONE, 5, "Mars", Category.TOY);

        when(productRepository.findAll()).thenReturn(List.of(entity1, entity2));
        when(productMapper.toDomainList(anyList())).thenReturn(List.of(p1, p2));

        List<Product> result = productService.findAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(p1, result.get(0));
        assertEquals(p2, result.get(1));

        verify(productRepository).findAll();
        verify(productMapper).toDomainList(anyList());
    }

    @Test
    @DisplayName("findProductById: should return product when exists")
    void testFindProductById_WhenExists_ReturnsProduct() {
        Long id = 1L;
        ProductEntity entity = new ProductEntity();
        entity.setId(id);

        Product product = new Product(id, "Milk", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);

        when(productRepository.findById(id)).thenReturn(Optional.of(entity));
        when(productMapper.toDomain(entity)).thenReturn(product);

        Product result = productService.findProductById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Milk", result.getName());

        verify(productRepository).findById(id);
        verify(productMapper).toDomain(entity);
    }

    @Test
    @DisplayName("findProductById: should throw exception when product not found")
    void testFindProductById_WhenNotFound_ThrowsException() {
        Long id = 999L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findProductById(id));

        verify(productRepository).findById(id);
        verifyNoInteractions(productMapper);
    }

    @Test
    @DisplayName("createProduct: should create product when name is unique")
    void testCreateProduct_WhenNameUnique_ReturnsCreatedProduct() {
        Product inputProduct = new Product(null, "Unique Name", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);
        ProductEntity entityToSave = new ProductEntity();
        ProductEntity savedEntity = new ProductEntity();
        savedEntity.setId(1L);

        Product expectedResult = new Product(1L, "Unique Name", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);

        when(productRepository.existsByNameIgnoreCase(inputProduct.getName())).thenReturn(false);
        when(productMapper.toEntity(inputProduct)).thenReturn(entityToSave);
        when(productRepository.save(entityToSave)).thenReturn(savedEntity);
        when(productMapper.toDomain(savedEntity)).thenReturn(expectedResult);

        Product result = productService.createProduct(inputProduct);

        assertNotNull(result.getId());
        assertEquals("Unique Name", result.getName());

        verify(productRepository).existsByNameIgnoreCase(inputProduct.getName());
        verify(productMapper).toEntity(inputProduct);
        verify(productRepository).save(entityToSave);
        verify(productMapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("createProduct: should throw exception when name is duplicate")
    void testCreateProduct_WhenNameDuplicate_ThrowsException() {
        Product newProduct = new Product(null, "Duplicate Name", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);

        when(productRepository.existsByNameIgnoreCase(newProduct.getName())).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class, () -> productService.createProduct(newProduct));

        verify(productRepository, never()).save(any());
        verifyNoInteractions(productMapper);
    }

    @Test
    @DisplayName("updateProduct: should update product when valid")
    void testUpdateProduct_WhenValid_ReturnsUpdatedProduct() {
        Long id = 1L;
        Product changes = new Product();
        changes.setName("New Name");

        ProductEntity existingEntity = new ProductEntity();
        existingEntity.setId(id);

        Product existingDomainProduct = new Product(id, "Old Name", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);
        ProductEntity updatedEntity = new ProductEntity();
        Product updatedDomainProduct = new Product(id, "New Name", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);

        when(productRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(productMapper.toDomain(existingEntity)).thenReturn(existingDomainProduct);
        when(productRepository.existsByNameIgnoreCase("New Name")).thenReturn(false);
        when(productMapper.toEntity(existingDomainProduct)).thenReturn(updatedEntity);
        when(productRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(productMapper.toDomain(updatedEntity)).thenReturn(updatedDomainProduct);

        Product result = productService.updateProduct(id, changes);

        assertEquals("New Name", result.getName());

        verify(productRepository).save(updatedEntity);
    }

    @Test
    @DisplayName("updateProduct: should throw exception when name conflict occurs")
    void testUpdateProduct_WhenNameConflict_ThrowsException() {
        Long id = 1L;
        Product changes = new Product();
        changes.setName("Taken Name");

        ProductEntity existingEntity = new ProductEntity();
        Product existingProduct = new Product(id, "Old Name", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);

        when(productRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(productMapper.toDomain(existingEntity)).thenReturn(existingProduct);
        when(productRepository.existsByNameIgnoreCase("Taken Name")).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class, () -> productService.updateProduct(id, changes));

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProduct: should update ALL fields when all are provided")
    void testUpdateProduct_AllFields_UpdatesEverything() {
        Long id = 1L;
        Product changes = new Product();
        changes.setName("New Name");
        changes.setDescription("New Desc");
        changes.setPrice(BigDecimal.TEN);
        changes.setStockQuantity(100);
        changes.setOriginPlanet("Mars");
        changes.setCategory(Category.TOY);

        ProductEntity existingEntity = new ProductEntity();
        Product existingDomainProduct = new Product(id, "Old", "Old", BigDecimal.ONE, 1, "Earth", Category.FOOD);

        Product finalDomainProduct = new Product(id, "New Name", "New Desc", BigDecimal.TEN, 100, "Mars", Category.TOY);
        ProductEntity finalEntity = new ProductEntity();

        when(productRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(productMapper.toDomain(existingEntity)).thenReturn(existingDomainProduct);
        when(productRepository.existsByNameIgnoreCase("New Name")).thenReturn(false);
        when(productMapper.toEntity(existingDomainProduct)).thenReturn(finalEntity);
        when(productRepository.save(finalEntity)).thenReturn(finalEntity);
        when(productMapper.toDomain(finalEntity)).thenReturn(finalDomainProduct);

        Product result = productService.updateProduct(id, changes);

        assertAll("Check all fields updated",
                () -> assertEquals("New Name", result.getName()),
                () -> assertEquals("New Desc", result.getDescription()),
                () -> assertEquals(BigDecimal.TEN, result.getPrice()),
                () -> assertEquals(100, result.getStockQuantity()),
                () -> assertEquals("Mars", result.getOriginPlanet()),
                () -> assertEquals(Category.TOY, result.getCategory())
        );
    }

    @Test
    @DisplayName("deleteProductById: should verify repository delete call")
    void testDeleteProductById_WhenCalled_VerifiesRepositoryCall() {
        Long id = 1L;

        productService.deleteProductById(id);

        verify(productRepository, times(1)).deleteById(id);
    }
}