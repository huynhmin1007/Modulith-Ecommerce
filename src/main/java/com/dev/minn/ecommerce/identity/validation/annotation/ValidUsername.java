package com.dev.minn.ecommerce.identity.validation.annotation;

import com.dev.minn.ecommerce.identity.validation.validator.UsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {

    String message() default "Username must be between 3 and 255 characters and can only contain letters, numbers, dots, hyphens, and underscores";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
