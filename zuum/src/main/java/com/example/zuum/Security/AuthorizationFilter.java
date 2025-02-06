package com.example.zuum.Security;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.zuum.Common.utils;
import com.example.zuum.Driver.DriverRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    DriverRepository driverRepository;

    public AuthorizationFilter(TokenService tokenService, DriverRepository driverRepository) {
        this.tokenService = tokenService;
        this.driverRepository = driverRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String baseRegex = "^/\\w*?/(\\d+)(/.*)?$";

        Pattern pattern = Pattern.compile(baseRegex);
        Matcher matcher = pattern.matcher(path);

        if (matcher.matches()) {
            String token = utils.recoverJWToken(request);
            Integer tokenId = tokenService.getSubject(token);
            
            Integer requestId = Integer.valueOf(matcher.group(1)); 

            if (!tokenId.equals(requestId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
  
}
