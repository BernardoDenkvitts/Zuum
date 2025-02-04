package com.example.zuum.Auth.Dto;

import java.time.LocalDate;

import com.example.zuum.User.CustomValidation.MaxAge;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserDTO(
    @NotBlank(message = "Name value cannot be blank")
    @NotNull(message = "Name value is necessary")
    String name,

    @NotBlank(message = "Email value cannot be blank")
    @NotNull(message = "Email value is necessary")
    @Email(message = "Invalid email")
    String email,
    
    @NotNull(message = "Password value is necessary")
    @Size(min = 5, max = 20, message = "Password must be between 5 and 20 characters")
    String password,

    @NotBlank(message = "Cellphone value cannot be blank")
    @NotNull(message = "Cellphone value is necessary")
    @Pattern(regexp="^[0-9]+$", message = "Invalid cellphone number")
    String cellphone,

    @NotNull(message = "Birthday value is necessary")
    @MaxAge(value = 100, message = "Maximum age allowed is 100")
    LocalDate birthday
) {}

