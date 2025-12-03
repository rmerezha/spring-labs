package com.rmerezha.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class CosmicWordCheckValidator implements ConstraintValidator<CosmicWordCheck, String> {

    private static final List<String> COSMIC_TERMS = List.of(
            "star", "galaxy", "comet", "space", "moon", "sun", "planet", "cosmo", "nebula"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String lowerCaseValue = value.toLowerCase();

        return COSMIC_TERMS.stream().anyMatch(lowerCaseValue::contains);
    }
}