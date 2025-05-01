package com.glowtique.glowtique.validation;

import com.glowtique.glowtique.web.dto.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidatorImpl implements ConstraintValidator<PasswordMatchValidator, RegisterRequest> {
    private String message;
    @Override
    public boolean isValid(RegisterRequest registerRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (registerRequest.getPassword() == null || registerRequest.getConfirmPassword() == null) {
            return false;
        }
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }

    @Override
    public void initialize(PasswordMatchValidator constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }
}
