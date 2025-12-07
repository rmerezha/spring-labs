package com.rmerezha.persistence.mapper;

import com.rmerezha.domain.Order;
import com.rmerezha.domain.OrderItem;
import com.rmerezha.persistence.entity.OrderEntity;
import com.rmerezha.persistence.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductEntityMapper.class})
public interface OrderEntityMapper {

    Order toDomain(OrderEntity entity);

    OrderEntity toEntity(Order domain);

    @Mapping(source = "product", target = "product")
    OrderItem toDomainItem(OrderItemEntity entity);

    @Mapping(source = "product", target = "product")
    OrderItemEntity toEntityItem(OrderItem domain);
}
