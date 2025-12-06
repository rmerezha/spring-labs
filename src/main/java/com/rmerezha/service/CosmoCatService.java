package com.rmerezha.service;

import com.rmerezha.aop.FeatureToggle;
import com.rmerezha.aop.Feature;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CosmoCatService {

    @FeatureToggle(Feature.COSMO_CATS)
    public List<String> getCosmoCats() {
        return List.of("Not Implemented");
    }

    @FeatureToggle(Feature.KITTY_PRODUCTS)
    public String getSecretKittyProduct() {
        return "Not Implemented";
    }
}
