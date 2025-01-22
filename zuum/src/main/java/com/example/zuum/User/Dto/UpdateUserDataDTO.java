package com.example.zuum.User.Dto;

import java.time.LocalDate;

import com.example.zuum.User.UserType;
import com.example.zuum.User.CustomValidation.MaxAge;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UpdateUserDataDTO {
    @NotBlank(message = "Name value cannot be blank")
    @NotNull(message = "Name value is necessary")
    private String name;

    @NotBlank(message = "Email value cannot be blank")
    @NotNull(message = "Email value is necessary")
    @Email(message = "Invalid email")
    private String email;

    @JsonProperty("type")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @NotBlank(message = "Cellphone value cannot be blank")
    @NotNull(message = "Cellphone value is necessary")
    @Pattern(regexp="^[0-9]+$", message = "Invalid cellphone number")
    private String cellphone;

    @NotNull(message = "Birthday value is necessary")
    @MaxAge(value = 100, message = "Maximum age allowed is 100")
    private LocalDate birthday;

    public UpdateUserDataDTO() {}

    public UpdateUserDataDTO(String name, String email, UserType userType, String cellphone, LocalDate birthday) {
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.cellphone = cellphone;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

}
