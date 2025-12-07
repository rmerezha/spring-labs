package com.rmerezha.mapper;

import com.rmerezha.domain.Product;
import com.rmerezha.dto.CreateProductDto;
import com.rmerezha.dto.ProductDetailsDto;
import com.rmerezha.dto.ProductListDto;
import com.rmerezha.dto.UpdateProductDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "originPlanet", source = "originPlanet", defaultValue = "Earth")
    @Mapping(target = "stockQuantity", source = "stockQuantity", defaultValue = "0")
    Product toDomain(CreateProductDto createProductDto);

    @Mapping(target = "id", ignore = true)
    Product toDomain(UpdateProductDto dto);

    ProductDetailsDto toDto(Product product);

    List<ProductDetailsDto> toDtoList(List<Product> products);

    default ProductListDto toProductListDto(List<Product> products) {
        return new ProductListDto(toDtoList(products));
    }
}
