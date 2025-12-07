package com.rmerezha.domain;

import lombok.Data;

@Data
public class CartItem {
    private Product product;
    private int quantity;
}
