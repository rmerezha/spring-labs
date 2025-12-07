package com.rmerezha.mapper;

import com.rmerezha.domain.Order;
import com.rmerezha.domain.OrderItem;
import com.rmerezha.dto.OrderDto;
import com.rmerezha.dto.ProductDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderMapper {

    @Mapping(source = "items", target = "products")
    OrderDto toDto(Order order);

    @Mapping(source = "products", target = "items")
    Order toDomain(OrderDto orderDto);

    default ProductDetailsDto mapItemToDto(OrderItem item, ProductMapper productMapper) {
        if (item == null || item.getProduct() == null) return null;
        return productMapper.toDto(item.getProduct());
    }

    default OrderItem mapDtoToItem(ProductDetailsDto dto, ProductMapper productMapper) {
        if (dto == null) return null;
        OrderItem item = new OrderItem();
        item.setProduct(productMapper.toDomain(dto));
        item.setQuantity(1);
        return item;
    }
}
