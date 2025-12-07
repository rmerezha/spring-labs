package com.rmerezha.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmerezha.domain.Category;
import com.rmerezha.domain.Product;
import com.rmerezha.dto.CreateProductDto;
import com.rmerezha.dto.ProductDetailsDto;
import com.rmerezha.dto.ProductListDto;
import com.rmerezha.dto.UpdateProductDto;
import com.rmerezha.mapper.ProductMapper;
import com.rmerezha.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Tag("web")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductMapper productMapper;

    @Test
    @DisplayName("getAllProducts: should return 200 and product list")
    void testGetAllProducts_WhenCalled_ReturnsList() throws Exception {
        Product product = new Product(1L, "Milk", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);
        ProductDetailsDto dto = new ProductDetailsDto(1L, "Milk", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);
        ProductListDto listDto = new ProductListDto(List.of(dto));

        when(productService.findAllProducts()).thenReturn(List.of(product));
        when(productMapper.toProductListDto(List.of(product))).thenReturn(listDto);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].name").value("Milk"));

        verify(productService).findAllProducts();
        verify(productMapper).toProductListDto(any());
    }

    @Test
    @DisplayName("getProductById: should return 200 and product details when exists")
    void testGetProductById_WhenExists_ReturnsDto() throws Exception {
        Long id = 1L;
        Product product = new Product(id, "Milk", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);
        ProductDetailsDto dto = new ProductDetailsDto(id, "Milk", "Desc", BigDecimal.TEN, 10, "Earth", Category.FOOD);

        when(productService.findProductById(id)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Milk"));

        verify(productService).findProductById(id);
    }

    @Test
    @DisplayName("createProduct: should return 201 and location header when valid")
    void testCreateProduct_WhenValid_ReturnsCreated() throws Exception {
        CreateProductDto createDto = new CreateProductDto(
                "Star Milk", "Desc", BigDecimal.TEN, 10, "Mars", Category.FOOD
        );
        Product mappedEntity = new Product(null, "Star Milk", "Desc", BigDecimal.TEN, 10, "Mars", Category.FOOD);
        Product savedEntity = new Product(101L, "Star Milk", "Desc", BigDecimal.TEN, 10, "Mars", Category.FOOD);
        ProductDetailsDto responseDto = new ProductDetailsDto(101L, "Star Milk", "Desc", BigDecimal.TEN, 10, "Mars", Category.FOOD);

        when(productMapper.toDomain(createDto)).thenReturn(mappedEntity);
        when(productService.createProduct(mappedEntity)).thenReturn(savedEntity);
        when(productMapper.toDto(savedEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/products/101"))
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.name").value("Star Milk"));

        verify(productMapper).toDomain(any(CreateProductDto.class));
        verify(productService).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("updateProduct: should return 200 and updated DTO")
    void testUpdateProduct_WhenValid_ReturnsUpdatedDto() throws Exception {
        Long id = 1L;
        UpdateProductDto updateDto = new UpdateProductDto(
                "New Name", "Desc", BigDecimal.valueOf(20), 5, "Mars", Category.TOY
        );
        Product changesEntity = new Product(null, "New Name", "Desc", BigDecimal.valueOf(20), 5, "Mars", Category.TOY);
        Product updatedEntity = new Product(id, "New Name", "Desc", BigDecimal.valueOf(20), 5, "Mars", Category.TOY);
        ProductDetailsDto responseDto = new ProductDetailsDto(id, "New Name", "Desc", BigDecimal.valueOf(20), 5, "Mars", Category.TOY);

        when(productMapper.toDomain(updateDto)).thenReturn(changesEntity);
        when(productService.updateProduct(eq(id), any(Product.class))).thenReturn(updatedEntity);
        when(productMapper.toDto(updatedEntity)).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));

        verify(productService).updateProduct(eq(id), any(Product.class));
    }

    @Test
    @DisplayName("deleteProduct: should return 204 No Content")
    void testDeleteProduct_WhenCalled_ReturnsNoContent() throws Exception {
        Long id = 1L;

        doNothing().when(productService).deleteProductById(id);

        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNoContent());

        verify(productService).deleteProductById(id);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("invalidCreateScenarios")
    @DisplayName("Create Validation: Should return 400 when input is invalid")
    void testCreateProduct_ValidationFailures(String testDescription, CreateProductDto invalidDto, String errorField) throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == '" + errorField + "')]").exists());

        verify(productService, never()).createProduct(any());
    }

    static Stream<Object[]> invalidCreateScenarios() {
        return Stream.of(
                new Object[]{"Name is empty",
                        new CreateProductDto("", "Desc", BigDecimal.TEN, 10, "Mars", Category.FOOD), "name"},

                new Object[]{"Name too long",
                        new CreateProductDto("A".repeat(101), "Desc", BigDecimal.TEN, 10, "Mars", Category.FOOD), "name"},

                new Object[]{"No cosmic word in name",
                        new CreateProductDto("Just Milk", "Desc", BigDecimal.TEN, 10, "Mars", Category.FOOD), "name"},

                new Object[]{"Description too long",
                        new CreateProductDto("Star Milk", "A".repeat(501), BigDecimal.TEN, 10, "Mars", Category.FOOD), "description"},

                new Object[]{"Price is null",
                        new CreateProductDto("Star Milk", "Desc", null, 10, "Mars", Category.FOOD), "price"},

                new Object[]{"Price is zero",
                        new CreateProductDto("Star Milk", "Desc", BigDecimal.ZERO, 10, "Mars", Category.FOOD), "price"},

                new Object[]{"Stock is negative",
                        new CreateProductDto("Star Milk", "Desc", BigDecimal.TEN, -1, "Mars", Category.FOOD), "stockQuantity"},

                new Object[]{"Planet name too long",
                        new CreateProductDto("Star Milk", "Desc", BigDecimal.TEN, 10, "A".repeat(51), Category.FOOD), "originPlanet"},

                new Object[]{"Category is null",
                        new CreateProductDto("Star Milk", "Desc", BigDecimal.TEN, 10, "Mars", null), "category"}
        );
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("invalidUpdateScenarios")
    @DisplayName("Update Validation: Should return 400 when input is invalid")
    void testUpdateProduct_ValidationFailures(String testDescription, UpdateProductDto invalidDto, String errorField) throws Exception {

        mockMvc.perform(put("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == '" + errorField + "')]").exists());

        verify(productService, never()).updateProduct(any(), any());
    }

    static Stream<Object[]> invalidUpdateScenarios() {
        return Stream.of(
                new Object[]{"Name too long",
                        new UpdateProductDto("A".repeat(101), null, null, null, null, null), "name"},

                new Object[]{"Description too long",
                        new UpdateProductDto(null, "A".repeat(501), null, null, null, null), "description"},

                new Object[]{"Price negative",
                        new UpdateProductDto(null, null, BigDecimal.valueOf(-5), null, null, null), "price"},

                new Object[]{"Stock negative",
                        new UpdateProductDto(null, null, null, -10, null, null), "stockQuantity"},

                new Object[]{"Planet too long",
                        new UpdateProductDto(null, null, null, null, "A".repeat(51), null), "originPlanet"}
        );
    }
}