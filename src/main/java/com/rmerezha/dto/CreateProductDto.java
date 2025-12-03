package com.rmerezha.dto;

import com.rmerezha.domain.Category;
import com.rmerezha.validation.CosmicWordCheck;
import jakarta.validation.constraints.*;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class CreateProductDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    @CosmicWordCheck
    String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    BigDecimal price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    Integer stockQuantity;

    @Size(max = 50, message = "Origin planet name is too long")
    String originPlanet;

    @NotNull(message = "Category is required")
    Category category;
}
