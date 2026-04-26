package com.almanatura.api.bootstrap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.config.AppProperties;
import com.almanatura.api.entity.User;
import com.almanatura.api.enums.Role;
import com.almanatura.api.repository.UserRepository;
import com.almanatura.api.validation.InternalPasswordPolicy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Idempotently provisions the initial super_user account from environment variables. Skipped when
 * admin credentials are not configured to avoid creating predictable accounts.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrapRunner implements ApplicationRunner {

    private final AppProperties properties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String email = properties.admin() == null ? null : properties.admin().email();
        String password = properties.admin() == null ? null : properties.admin().password();

        if (isBlank(email) || isBlank(password)) {
            log.warn("Skipping admin bootstrap: APP_ADMIN_EMAIL / APP_ADMIN_PASSWORD are not set");
            return;
        }

        try {
            InternalPasswordPolicy.validateOrThrow(password);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "APP_ADMIN_PASSWORD does not meet the internal password policy: "
                            + ex.getMessage()
                            + ". See "
                            + InternalPasswordPolicy.REQUIREMENTS_MESSAGE,
                    ex);
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            log.info("Admin bootstrap: super_user '{}' already exists, nothing to do", email);
            return;
        }

        User admin =
                User.builder()
                        .name("Super User")
                        .email(email)
                        .passwordHash(passwordEncoder.encode(password))
                        .role(Role.SUPER_USER)
                        .enabled(true)
                        .build();
        userRepository.save(admin);
        log.info("Admin bootstrap: created initial super_user '{}'", email);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
