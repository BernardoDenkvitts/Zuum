package com.example.zuum.Security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.zuum.User.UserModel;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;
    private final String issuer = "zuum-api";

    public String generateToken(UserModel user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String token = JWT.create()
                    .withIssuer(issuer)
                    .withSubject(String.valueOf(user.getId()))
                    .withClaim("email", user.getEmail())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            
            return token;
        } catch(JWTCreationException ex) {
            throw new RuntimeException("Error while generating token", ex);
        }
    }

    public DecodedJWT validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        
        return JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
                .verify(token);
    }

    public String getEmail(String token) {
        DecodedJWT decoded = validateToken(token);
        
        return decoded.getClaim("email").asString();
    }

    public Integer getSubject(String token) {
        DecodedJWT decoded = validateToken(token);
        
        return Integer.valueOf(decoded.getSubject());
    }

    private Instant genExpirationDate() {
        ZoneId sysZone = ZoneId.systemDefault();
        ZoneOffset offset = sysZone.getRules().getOffset(LocalDateTime.now());

        return LocalDateTime.now().plusHours(5).toInstant(offset);
    }
}
