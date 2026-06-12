package com.todotask.backend.security.service;

import com.todotask.backend.security.jwt.JwtUtil;
import com.todotask.backend.security.dto.LoginRequest;
import com.todotask.backend.security.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        String token = jwtUtil.generateToken(request.email());
        return new LoginResponse(token);
    }
}
