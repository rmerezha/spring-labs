package com.rmerezha.service;

import com.rmerezha.PostgresIT;
import com.rmerezha.domain.Cart;
import com.rmerezha.exception.CartNotFoundException;
import com.rmerezha.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class CartServiceIT extends PostgresIT {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
    }

    @Test
    @DisplayName("createCart: should create cart when valid")
    void testCreateCart_WhenValid_ReturnsCreatedCart() {
        Cart newCart = new Cart();

        Cart created = cartService.createCart(newCart);

        assertNotNull(created.getId());
        assertEquals(1, cartRepository.count());
    }

    @Test
    @DisplayName("findCartById: should return cart when exists")
    void testFindCartById_WhenExists_ReturnsCart() {
        Cart cart = cartService.createCart(new Cart());

        Cart found = cartService.findCartById(cart.getId());

        assertNotNull(found);
        assertEquals(cart.getId(), found.getId());
    }

    @Test
    @DisplayName("findCartById: should throw exception when cart not found")
    void testFindCartById_WhenNotFound_ThrowsException() {
        assertThrows(CartNotFoundException.class, () -> {
            cartService.findCartById(999L);
        });
    }

    @Test
    @DisplayName("updateCart: should update cart items (replace logic)")
    void testUpdateCart_WhenValid_ReturnsUpdatedCart() {
        Cart original = cartService.createCart(new Cart());

        Cart changes = new Cart();

        Cart updated = cartService.updateCart(original.getId(), changes);

        assertNotNull(updated);
        assertEquals(original.getId(), updated.getId());
    }

    @Test
    @DisplayName("deleteCartById: should delete cart from database")
    void testDeleteCartById_WhenCalled_DeletesEntity() {
        Cart cart = cartService.createCart(new Cart());

        cartService.deleteCartById(cart.getId());

        assertFalse(cartRepository.existsById(cart.getId()));
    }

    @Test
    @DisplayName("deleteCartById: should throw exception if cart does not exist")
    void testDeleteCartById_WhenNotFound_ThrowsException() {
        assertThrows(CartNotFoundException.class, () -> {
            cartService.deleteCartById(999L);
        });
    }

    @Test
    @DisplayName("clearCart: should remove all items from cart but keep cart")
    void testClearCart_WhenCalled_RemovesItems() {
        Cart cart = cartService.createCart(new Cart());

        cartService.clearCart(cart.getId());

        Cart cleared = cartService.findCartById(cart.getId());
        assertNotNull(cleared);
        assertTrue(cleared.getItems() == null || cleared.getItems().isEmpty());

        assertTrue(cartRepository.existsById(cart.getId()));
    }
}
