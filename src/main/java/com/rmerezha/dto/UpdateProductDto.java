package com.rmerezha.dto;

import com.rmerezha.domain.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class UpdateProductDto {

    @Size(max = 100)
    String name;

    @Size(max = 500)
    String description;

    @DecimalMin(value = "0.01")
    BigDecimal price;

    @Min(value = 0)
    Integer stockQuantity;

    @Size(max = 50)
    String originPlanet;

    Category category;
}