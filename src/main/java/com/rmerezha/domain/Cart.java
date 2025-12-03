package com.rmerezha.domain;

import lombok.Data;
import java.util.List;

@Data
public class Cart {
    private Long id;
    private List<Product> products;
}
