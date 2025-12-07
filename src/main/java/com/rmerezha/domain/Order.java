package com.rmerezha.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private Long id;
    private List<OrderItem> items;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;
}