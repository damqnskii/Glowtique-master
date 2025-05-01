package com.glowtique.glowtique.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchValidatorImpl.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatchValidator {
    String message() default "Паролите Ви не съвпадат!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
