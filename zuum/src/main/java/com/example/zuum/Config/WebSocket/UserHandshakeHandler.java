package com.example.zuum.Config.WebSocket;

import com.sun.security.auth.UserPrincipal;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.example.zuum.Common.utils;

public class UserHandshakeHandler extends DefaultHandshakeHandler {

    static final Logger LOGGER = utils.getLogger(UserHandshakeHandler.class);

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String randomId = UUID.randomUUID().toString();

        LOGGER.info("User with ID {} connected", randomId);
        return new UserPrincipal(randomId);
    }

}
