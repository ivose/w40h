package com.example.some.security;

import com.example.some.entities.User;
import com.example.some.util.security.JwtUtils;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private final JwtUtils jwtUtils;

    public JwtTokenProvider(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public String generateToken(User user) {
        return jwtUtils.generateToken(user.getId().toString(), user.getEmail());
    }

    public Long getUserIdFromToken(String token) {
        return jwtUtils.getUserIdFromToken(token);
    }

    public String getRoleFromToken(String token) {
        return "See roll"; // Maintained as per original implementation
    }

    public boolean validateToken(String token) {
        return jwtUtils.isTokenValid(token);
    }
}