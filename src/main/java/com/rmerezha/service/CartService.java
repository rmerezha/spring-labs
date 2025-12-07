package com.rmerezha.service;

import com.rmerezha.domain.Cart;

public interface CartService {
    Cart findCartById(Long id);
    Cart createCart(Cart cart);
    Cart updateCart(Long id, Cart cartChanges);
    void deleteCartById(Long id);
    void clearCart(Long id);
}