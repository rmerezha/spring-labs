package com.rmerezha.service;

import com.rmerezha.aop.Feature;
import com.rmerezha.config.FeatureProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeatureToggleServiceImpl implements FeatureToggleService {

    private final FeatureProperties featureProperties;

    public boolean isFeatureEnabled(Feature feature) {
        return featureProperties.getToggles().getOrDefault(feature.getKey(), false);
    }
}