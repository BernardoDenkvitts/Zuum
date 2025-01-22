package com.example.zuum.User;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.zuum.User.Dto.UpdateUserDataDTO;
import com.example.zuum.User.Dto.UserResponseDTO;

@RestController
@RequestMapping("/users")
public record UserController(UserService service) {
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateInformations(@PathVariable Integer id, @RequestBody @Validated UpdateUserDataDTO dto) {
        UserModel user = service.updateInformations(id, dto);
        return ResponseEntity.ok(UserResponseDTO.create(user));
    }
}
