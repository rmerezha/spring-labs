package com.rmerezha.dto;

import lombok.Value;
import java.math.BigDecimal;
import java.util.List;

@Value
public class OrderDto {
    Long id;
    List<ProductDetailsDto> products;
    BigDecimal totalPrice;
    String status;
}