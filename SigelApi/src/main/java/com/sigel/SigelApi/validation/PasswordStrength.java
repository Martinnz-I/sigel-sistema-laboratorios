package com.sigel.SigelApi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Documented
public @interface PasswordStrength {
    String message() default "La contraseña debe contener al menos una mayúscula y un número";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
