package com.rmerezha.dto;

import lombok.Value;

import java.util.List;

@Value
public class CartDto {
    Long id;
    List<ProductDetailsDto> products;
}