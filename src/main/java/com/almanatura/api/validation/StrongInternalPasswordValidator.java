package com.almanatura.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongInternalPasswordValidator
        implements ConstraintValidator<StrongInternalPassword, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return InternalPasswordPolicy.isValid(value);
    }
}
