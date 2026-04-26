package com.almanatura.api.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Provides the {@link AuditorAware} bean used by Spring Data JPA auditing to populate
 * {@code @CreatedBy}/{@code @LastModifiedBy}.
 *
 * <p>Returns the email of the currently authenticated principal, or {@code "system"} for
 * non-authenticated flows (bootstrapping, scheduled jobs, public endpoints).
 */
@Configuration
public class AuditorAwareConfig {

    private static final String SYSTEM_AUDITOR = "system";

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null
                    || !auth.isAuthenticated()
                    || "anonymousUser".equals(auth.getPrincipal())) {
                return Optional.of(SYSTEM_AUDITOR);
            }
            return Optional.of(auth.getName());
        };
    }
}
