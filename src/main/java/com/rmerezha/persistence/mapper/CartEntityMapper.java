package com.rmerezha.persistence.mapper;

import com.rmerezha.domain.Cart;
import com.rmerezha.domain.CartItem;
import com.rmerezha.persistence.entity.CartEntity;
import com.rmerezha.persistence.entity.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductEntityMapper.class})
public interface CartEntityMapper {

    Cart toDomain(CartEntity entity);

    CartEntity toEntity(Cart domain);

    @Mapping(source = "product", target = "product")
    CartItem toDomainItem(CartItemEntity entity);

    @Mapping(source = "product", target = "product")
    CartItemEntity toEntityItem(CartItem domain);
}
