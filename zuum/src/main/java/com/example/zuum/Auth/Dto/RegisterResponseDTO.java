package com.example.zuum.Auth.Dto;

import com.example.zuum.User.UserModel;
import com.example.zuum.User.Dto.UserResponseDTO;

public class RegisterResponseDTO extends UserResponseDTO {
    private String password;

    public RegisterResponseDTO() {}

    public RegisterResponseDTO(UserModel userModel, String password) {
        super(userModel.getId(), userModel.getName(), userModel.getEmail(), userModel.getUserType(), userModel.getCellphone(), userModel.getBirthday());
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
