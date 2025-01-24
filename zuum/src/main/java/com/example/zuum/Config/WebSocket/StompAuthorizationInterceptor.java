package com.example.zuum.Config.WebSocket;

import org.slf4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.example.zuum.Common.utils;
import com.sun.security.auth.UserPrincipal;

@Component
public class StompAuthorizationInterceptor implements ChannelInterceptor {
    static final Logger LOGGER = utils.getLogger(StompAuthorizationInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String id = accessor.getFirstNativeHeader("Authorization");

            if (id == null) {
                throw new IllegalArgumentException("Authorization header is required");
            }

            LOGGER.info("User with ID {} connected", id);
            accessor.setUser(new UserPrincipal(id));
        }

        return message;
    }

}
