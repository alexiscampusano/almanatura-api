package com.almanatura.api.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.LoginRequest;
import com.almanatura.api.dto.LoginResponse;
import com.almanatura.api.dto.UserSummary;
import com.almanatura.api.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description =
                "Login is public; other operations under /auth require a valid JWT (Bearer) for an"
                        + " internal user.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(
            summary = "Authenticate an internal user",
            description =
                    "Validates the email and password of an internal user (super_user or"
                            + " event_manager) and returns a signed JWT plus a safe user"
                            + " projection. Failures return RFC 7807 problem responses with the"
                            + " machine-readable codes INVALID_CREDENTIALS, ACCOUNT_DISABLED or"
                            + " VALIDATION_FAILED.")
    public LoginResponse login(@Valid @RequestBody LoginRequest body) {
        return authService.login(body);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Current authenticated user",
            description =
                    "Returns the same safe projection as `user` in the login response, loaded from"
                        + " the database. Requires `Authorization: Bearer <JWT>`. Returns 404 if"
                        + " the account was removed after the token was issued.")
    public UserSummary me() {
        return authService.getCurrentUser();
    }
}
