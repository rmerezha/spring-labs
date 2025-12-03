package com.rmerezha.dto;

import com.rmerezha.domain.Category;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class ProductDetailsDto {
    Long id;
    String name;
    String description;
    BigDecimal price;
    Integer stockQuantity;
    String originPlanet;
    Category category;
}
