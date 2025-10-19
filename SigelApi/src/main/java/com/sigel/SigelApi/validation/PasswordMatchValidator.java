package com.sigel.SigelApi.validation;

import com.sigel.SigelApi.dto.RegistroRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, RegistroRequest> {

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {}

    @Override
    public boolean isValid(RegistroRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        if (request.getPassword() == null || request.getConfirmarPassword() == null) {
            return false;
        }

        return request.getPassword().equals(request.getConfirmarPassword());
    }
}