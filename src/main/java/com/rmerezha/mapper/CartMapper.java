package com.rmerezha.mapper;

import com.rmerezha.domain.Cart;
import com.rmerezha.domain.CartItem;
import com.rmerezha.dto.CartDto;
import com.rmerezha.dto.ProductDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartMapper {

    @Mapping(source = "items", target = "products")
    CartDto toDto(Cart cart);

    @Mapping(source = "products", target = "items")
    Cart toDomain(CartDto cartDto);

    default ProductDetailsDto mapItemToDto(CartItem item, ProductMapper productMapper) {
        if (item == null || item.getProduct() == null) return null;
        return productMapper.toDto(item.getProduct());
    }

    default CartItem mapDtoToItem(ProductDetailsDto dto, ProductMapper productMapper) {
        if (dto == null) return null;
        CartItem item = new CartItem();
        item.setProduct(productMapper.toDomain(dto));
        item.setQuantity(1);
        return item;
    }
}