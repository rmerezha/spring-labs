package com.rmerezha.domain;

import lombok.Data;

@Data
public class OrderItem {
    private Product product;
    private int quantity;
}
