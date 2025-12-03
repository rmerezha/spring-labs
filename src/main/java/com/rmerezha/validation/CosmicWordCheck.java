package com.rmerezha.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CosmicWordCheckValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CosmicWordCheck {

    String message() default "Name must contain a cosmic term (star, galaxy, comet, etc.)";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}