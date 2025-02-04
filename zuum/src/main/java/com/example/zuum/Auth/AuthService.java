package com.example.zuum.Auth;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.zuum.Auth.Dto.LoginDTO;
import com.example.zuum.Auth.Dto.RegisterUserDTO;
import com.example.zuum.Security.TokenService;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserRepository;
import com.example.zuum.User.Exception.EmailAlreadyInUseException;

@Service
public class AuthService implements UserDetailsService {

    private ApplicationContext context;
    private final UserRepository repository;
    private final TokenService tokenService;
    
    private AuthenticationManager authenticationManager;

    public AuthService(ApplicationContext context, UserRepository repository, TokenService tokenService) {
        this.context = context;
        this.repository = repository;
        this.tokenService = tokenService;
    }

    public UserModel create(RegisterUserDTO dto) {
        if (repository.findByEmail(dto.email()).isPresent()) throw new EmailAlreadyInUseException();

        String encryptedPassword = new BCryptPasswordEncoder().encode(dto.password());
        UserModel newUser = repository.save(new UserModel(dto.name(), dto.email(), encryptedPassword, dto.cellphone(), dto.birthday()));

    
        return newUser;
    }

    public String login(LoginDTO dto) {
        // Necessary to avoid dependency cycle
        authenticationManager = context.getBean(AuthenticationManager.class);
        
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((UserModel) auth.getPrincipal());

        return token;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

}