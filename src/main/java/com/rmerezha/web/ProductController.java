package com.rmerezha.web;

import com.rmerezha.domain.Product;
import com.rmerezha.dto.CreateProductDto;
import com.rmerezha.dto.ProductDetailsDto;
import com.rmerezha.dto.ProductListDto;
import com.rmerezha.dto.UpdateProductDto;
import com.rmerezha.mapper.ProductMapper;
import com.rmerezha.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<ProductListDto> getAllProducts() {
        List<Product> products = productService.findAllProducts();
        return ResponseEntity.ok(productMapper.toProductListDto(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsDto> getProductById(@PathVariable Long id) {
        Product product = productService.findProductById(id);
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @PostMapping
    public ResponseEntity<ProductDetailsDto> createProduct(
            @Valid @RequestBody CreateProductDto createProductDto
    ) {
        Product productToCreate = productMapper.toEntity(createProductDto);

        Product createdProduct = productService.createProduct(productToCreate);

        ProductDetailsDto responseDto = productMapper.toDto(createdProduct);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.getId())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDetailsDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductDto updateProductDto
    ) {
        Product productChanges = productMapper.toEntity(updateProductDto);

        Product updatedProduct = productService.updateProduct(id, productChanges);

        return ResponseEntity.ok(productMapper.toDto(updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}
