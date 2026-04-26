package com.almanatura.api.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.config.AppProperties;
import com.almanatura.api.dto.LoginRequest;
import com.almanatura.api.dto.LoginResponse;
import com.almanatura.api.dto.UserSummary;
import com.almanatura.api.entity.User;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.repository.UserRepository;
import com.almanatura.api.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Encapsulates internal user authentication. Delegates the actual credential check to the
 * configured {@link AuthenticationManager} (which uses {@code DaoAuthenticationProvider} + BCrypt)
 * and returns a signed JWT plus a safe user projection.
 *
 * <p>Authentication failures propagate as Spring Security exceptions and are translated to RFC 7807
 * problem responses by {@code GlobalExceptionHandler}; this class does not catch them to avoid
 * duplicating that mapping.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TOKEN_TYPE_BEARER = "Bearer";
    private static final long MILLIS_PER_SECOND = 1000L;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppProperties appProperties;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(), request.password()));

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        long expiresInSeconds = appProperties.jwt().expirationMs() / MILLIS_PER_SECOND;

        log.info("Issued JWT for user '{}' (role={})", user.getEmail(), user.getRole());

        return new LoginResponse(
                token,
                TOKEN_TYPE_BEARER,
                expiresInSeconds,
                new UserSummary(user.getId(), user.getEmail(), user.getName(), user.getRole()));
    }

    /**
     * Returns the current internal user from the security context, re-loaded from the database so
     * enabled flag and profile fields stay consistent with persistence.
     */
    @Transactional(readOnly = true)
    public UserSummary getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User principal)) {
            throw new IllegalStateException("Expected an authenticated User principal");
        }
        User user =
                userRepository
                        .findByEmailIgnoreCase(principal.getEmail())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User no longer exists: " + principal.getEmail()));
        return new UserSummary(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
