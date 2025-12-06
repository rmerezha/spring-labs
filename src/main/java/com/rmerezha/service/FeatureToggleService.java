package com.rmerezha.service;

import com.rmerezha.aop.Feature;

public interface FeatureToggleService {

    boolean isFeatureEnabled(Feature feature);

}
