package com.example.zuum.User.CustomValidation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxAgeValidator.class)
public @interface MaxAge {
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int value() default 100;
    String message();
}
