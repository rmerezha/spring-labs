package com.rmerezha.persistence.entity;

import com.rmerezha.domain.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products",
        indexes = {
                @Index(name = "idx_product_name", columnList = "name")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_product_name_category", columnNames = {"name", "category"})
        }
)
@Getter
@Setter
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_seq_gen")
    @SequenceGenerator(
            name = "products_seq_gen",
            sequenceName = "products_seq",
            allocationSize = 50,
            initialValue = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "origin_planet")
    private String originPlanet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
}