package com.almanatura.api.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.almanatura.api.security.JwtService;

import lombok.RequiredArgsConstructor;

/**
 * Skeleton authentication service. Endpoint wiring (POST /auth/login) is implemented in Task 10.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
}
