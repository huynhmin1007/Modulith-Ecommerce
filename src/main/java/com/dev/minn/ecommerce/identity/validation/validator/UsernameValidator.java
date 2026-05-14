package com.dev.minn.ecommerce.identity.validation.validator;

import com.dev.minn.ecommerce.identity.validation.annotation.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9._-]+$";

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {

        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        if (username.length() < 3 || username.length() > 255) {
            return false;
        }

        return username.matches(USERNAME_PATTERN);
    }
}
