package com.rmerezha.mapper;

import com.rmerezha.domain.Order;
import com.rmerezha.dto.OrderDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderMapper {

    OrderDto toDto(Order order);

    Order toDomain(OrderDto orderDto);
}
