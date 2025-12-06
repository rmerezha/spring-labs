package com.rmerezha.aop;

import com.rmerezha.exception.FeatureNotAvailableException;
import com.rmerezha.service.FeatureToggleService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class FeatureToggleAspect {

    private final FeatureToggleService featureService;

    @Around("@annotation(featureToggle)")
    public Object checkFeatureToggle(ProceedingJoinPoint joinPoint, FeatureToggle featureToggle) throws Throwable {

        var feature = featureToggle.value();

        if (featureService.isFeatureEnabled(feature)) {
            return joinPoint.proceed();
        } else {
            throw new FeatureNotAvailableException("Feature " + feature.getKey() + " is currently disabled");
        }
    }
}