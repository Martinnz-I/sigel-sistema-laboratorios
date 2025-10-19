package com.sigel.SigelApi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {

    @Override
    public void initialize(PasswordStrength constraintAnnotation) {}

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        boolean tieneUppercase = password.matches(".*[A-Z].*");
        boolean tieneNumero = password.matches(".*[0-9].*");

        return tieneUppercase && tieneNumero;
    }
}