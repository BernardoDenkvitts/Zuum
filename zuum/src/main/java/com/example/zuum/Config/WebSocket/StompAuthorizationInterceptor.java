package com.example.zuum.Config.WebSocket;

import org.slf4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.example.zuum.Common.utils;
import com.example.zuum.Security.TokenService;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserRepository;
import com.example.zuum.User.UserType;

@Component
public class StompAuthorizationInterceptor implements ChannelInterceptor {
    
    private final TokenService tokenService;
    private final UserRepository userRepository;
    static final Logger log = utils.getLogger(StompAuthorizationInterceptor.class);

    public StompAuthorizationInterceptor(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            StompCommand command = accessor.getCommand();

            if (StompCommand.CONNECT.equals(command)) {
                authenticateUser(accessor);
            }
            else if (StompCommand.SUBSCRIBE.equals(command)) {
                authorizeSubscription(accessor);
            };
        }

        return message;
    }

    private void authenticateUser(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");

        if (authHeader == null) throw new MessagingException("Missing Authorization header");
        
        String token = authHeader.substring(7);
        try {
            String email = tokenService.getEmail(token);
            
            UserDetails user = userRepository.findByEmail(email).orElse(null);

            if (user == null) throw new MessagingException("Unauthorized");

            var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            accessor.setUser(auth);

            log.info("User with identifier {} connected", auth.getName());
        } catch (Exception e) {
            throw new MessagingException("Invalid token");
        }
    }

    private void authorizeSubscription(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) accessor.getUser();

        if (auth == null || auth.getPrincipal() == null) {
            throw new MessagingException("Unauthorized");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserModel user = (UserModel) userDetails;

        if (destination != null && destination.startsWith("/user/queue/driver") && user.getUserType() == UserType.PASSENGER) {
            throw new MessagingException("Passengers are not allowed to subscribe to this topic");
        }
    }

}
