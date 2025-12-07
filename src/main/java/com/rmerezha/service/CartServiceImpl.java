package com.rmerezha.service;

import com.rmerezha.domain.Cart;
import com.rmerezha.exception.CartNotFoundException;
import com.rmerezha.persistence.mapper.CartEntityMapper;
import com.rmerezha.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartEntityMapper cartMapper;

    @Override
    @Transactional(readOnly = true)
    public Cart findCartById(Long id) {
        var entity = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException(id));
        return cartMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public Cart createCart(Cart cart) {
        var entity = cartMapper.toEntity(cart);
        var savedEntity = cartRepository.save(entity);
        return cartMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public Cart updateCart(Long id, Cart cartChanges) {
        Cart existingCart = findCartById(id);

        if (cartChanges.getItems() != null) {

            existingCart.setItems(cartChanges.getItems());
        }

        var entity = cartMapper.toEntity(existingCart);
        var savedEntity = cartRepository.save(entity);
        return cartMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public void deleteCartById(Long id) {
        if (!cartRepository.existsById(id)) {
            throw new CartNotFoundException(id);
        }
        cartRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void clearCart(Long id) {
        var entity = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException(id));

        entity.getItems().clear();
        cartRepository.save(entity);
    }
}
