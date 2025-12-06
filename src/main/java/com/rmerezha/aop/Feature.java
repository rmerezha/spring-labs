package com.rmerezha.aop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Feature {

    COSMO_CATS("cosmo-cats"),
    KITTY_PRODUCTS("kitty-products");

    private final String key;
}