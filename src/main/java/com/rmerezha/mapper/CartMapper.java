package com.rmerezha.mapper;

import com.rmerezha.domain.Cart;
import com.rmerezha.dto.CartDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartMapper {

    CartDto toDto(Cart cart);

    Cart toDomain(CartDto cartDto);
}
