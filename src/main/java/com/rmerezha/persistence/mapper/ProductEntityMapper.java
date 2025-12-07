package com.rmerezha.persistence.mapper;

import com.rmerezha.domain.Product;
import com.rmerezha.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

    Product toDomain(ProductEntity entity);
    List<Product> toDomainList(List<ProductEntity> entities);

    ProductEntity toEntity(Product domain);
}
