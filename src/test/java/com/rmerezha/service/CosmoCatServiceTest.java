package com.rmerezha.service;

import com.rmerezha.aop.FeatureToggleAspect;
import com.rmerezha.aop.Feature;
import com.rmerezha.exception.FeatureNotAvailableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CosmoCatService.class, FeatureToggleAspect.class})
@EnableAspectJAutoProxy
class CosmoCatServiceTest {

    @Autowired
    private CosmoCatService cosmoCatService;

    @MockitoBean
    private FeatureToggleService featureService;

    @Test
    @DisplayName("getCosmoCats: should return list when feature is ENABLED")
    void testGetCosmoCats_WhenEnabled_ReturnsList() {
        when(featureService.isFeatureEnabled(Feature.COSMO_CATS)).thenReturn(true);

        List<String> result = cosmoCatService.getCosmoCats();

        assertNotNull(result);
    }

    @Test
    @DisplayName("getCosmoCats: should throw exception when feature is DISABLED")
    void testGetCosmoCats_WhenDisabled_ThrowsException() {
        when(featureService.isFeatureEnabled(Feature.COSMO_CATS)).thenReturn(false);

        assertThrows(FeatureNotAvailableException.class, () -> cosmoCatService.getCosmoCats());
    }

    @Test
    @DisplayName("getSecretKittyProduct: should return product when feature is ENABLED")
    void testGetSecretKittyProduct_WhenEnabled_ReturnsProduct() {
        when(featureService.isFeatureEnabled(Feature.KITTY_PRODUCTS)).thenReturn(true);

        String result = cosmoCatService.getSecretKittyProduct();

        assertNotNull(result);
    }

    @Test
    @DisplayName("getSecretKittyProduct: should throw exception when feature is DISABLED")
    void testGetSecretKittyProduct_WhenDisabled_ThrowsException() {
        when(featureService.isFeatureEnabled(Feature.KITTY_PRODUCTS)).thenReturn(false);

        assertThrows(FeatureNotAvailableException.class, () -> cosmoCatService.getSecretKittyProduct());
    }
}