package com.example.zuum.Auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.zuum.Driver.DriverRepository;

@RestController
@RequestMapping("/auth")
public record AuthController(DriverRepository repository) {
}
