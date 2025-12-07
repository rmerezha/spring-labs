package com.rmerezha.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Order {
    private Long id;
    private List<OrderItem> products;
    private BigDecimal totalPrice;
    private String status;
}