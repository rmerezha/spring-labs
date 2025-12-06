package com.rmerezha.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carts_seq_gen")
    @SequenceGenerator(
            name = "carts_seq_gen",
            sequenceName = "carts_seq",
            allocationSize = 50,
            initialValue = 1
    )
    private Long id;

    // Тут може бути прив'язка до User, наприклад:
    // @Column(name = "user_id", unique = true)
    // private Long userId;

    // Кошик теж зручно зберігати каскадом (PERSIST або ALL, бо кошик - це тимчасова штука)
    // Тут можна використати CascadeType.ALL + orphanRemoval = true,
    // тому що якщо ми видаляємо товар з кошика, запис CartItem має зникнути.
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemEntity> items = new ArrayList<>();

    // --- Helper Methods ---
    public void addItem(CartItemEntity item) {
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItemEntity item) {
        items.remove(item);
        item.setCart(null);
    }
}
