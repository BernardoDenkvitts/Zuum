package com.example.zuum.User.CustomValidation;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class MaxAgeValidator implements ConstraintValidator<MaxAge, LocalDate> {
    private int maxAge;

    @Override
    public void initialize(MaxAge constraintAnnotation) {
        this.maxAge = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        System.out.println("TESTANDO AQUI AMIGO");

        if (value == null) {
            return true;
        }

        LocalDate currentDate = LocalDate.now();
        return Period.between(value, currentDate).getYears() <= maxAge;
    }
}
