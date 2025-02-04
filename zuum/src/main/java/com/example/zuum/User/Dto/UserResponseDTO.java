package com.example.zuum.User.Dto;

import java.time.LocalDate;

import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;

public class UserResponseDTO {
    private Integer id;
    private String name;
    private String email;
    private UserType userType;
    private String cellphone;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate birthday;

    public UserResponseDTO() {}

    public UserResponseDTO(Integer id, String name, String email, UserType userType, String cellphone,
            LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.cellphone = cellphone;
        this.birthday = birthday;
    }

    public static UserResponseDTO create(UserModel model) {
        return new UserResponseDTO(model.getId(), model.getName(), model.getEmail(), model.getUserType(), model.getCellphone(), model.getBirthday());
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UserType getUserType() {
        return userType;
    }

    public String getCellphone() {
        return cellphone;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    
}
