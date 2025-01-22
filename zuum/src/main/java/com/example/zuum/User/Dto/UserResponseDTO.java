package com.example.zuum.User.Dto;

import java.time.LocalDate;

import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;

public record UserResponseDTO(
    Integer id,
    String name,
    String email,
    UserType userType,
    String cellphone,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LocalDate birthday
) {
    public static UserResponseDTO create(UserModel model) {
        return new UserResponseDTO(model.getId(), model.getName(), model.getEmail(), model.getUserType(), model.getCellphone(), model.getBirthday());
    }
}
