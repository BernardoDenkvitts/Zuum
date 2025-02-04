package com.example.zuum.Auth;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.zuum.Auth.Dto.LoginDTO;
import com.example.zuum.Auth.Dto.LoginResponseDTO;
import com.example.zuum.Auth.Dto.RegisterResponseDTO;
import com.example.zuum.Auth.Dto.RegisterUserDTO;
import com.example.zuum.User.UserModel;

@RestController
@RequestMapping("/auth")
public record AuthController(AuthService service) {

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody @Validated RegisterUserDTO dto) {
        UserModel newUser = service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/{id}").buildAndExpand(newUser.getId()).toUri();

        return ResponseEntity.created(location).body(new RegisterResponseDTO(newUser, newUser.getPassword())); 
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Validated LoginDTO dto) {
        String token = service.login(dto);

        return ResponseEntity.ok(new LoginResponseDTO(token)); 
    }

}
