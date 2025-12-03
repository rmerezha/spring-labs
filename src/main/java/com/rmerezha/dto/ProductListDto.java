package com.rmerezha.dto;

import lombok.Value;
import java.util.List;

@Value
public class ProductListDto {
    List<ProductDetailsDto> products;
}