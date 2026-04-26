package com.almanatura.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.almanatura.api.security.CustomUserDetailsService;
import com.almanatura.api.security.JwtAccessDeniedHandler;
import com.almanatura.api.security.JwtAuthenticationEntryPoint;
import com.almanatura.api.security.JwtAuthenticationFilter;
import com.almanatura.api.security.RateLimitFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitFilter rateLimitFilter;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UrlBasedCorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(
                        ex ->
                                ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(HttpMethod.POST, "/auth/login")
                                        .permitAll()
                                        // Harness path for ErrorResponseTest (controller lives in
                                        // test sources only;
                                        // production has no mapping here → 404).
                                        .requestMatchers("/auth/test/**")
                                        .permitAll()
                                        .requestMatchers("/auth/**")
                                        .authenticated()
                                        .requestMatchers(HttpMethod.GET, "/events", "/events/**")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.POST, "/events/*/register")
                                        .permitAll()
                                        .requestMatchers("/ping")
                                        .permitAll()
                                        .requestMatchers("/actuator/health/**", "/actuator/info")
                                        .permitAll()
                                        .requestMatchers(
                                                "/swagger-ui.html",
                                                "/swagger-ui/**",
                                                "/api-docs/**",
                                                "/v3/api-docs/**")
                                        .permitAll()
                                        .requestMatchers("/admin/users/**")
                                        .hasRole("SUPER_USER")
                                        .requestMatchers("/admin/**")
                                        .hasAnyRole("SUPER_USER", "EVENT_MANAGER")
                                        .anyRequest()
                                        .authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        // Map unknown emails to BadCredentials so login always returns INVALID_CREDENTIALS (401)
        // and
        // never leaks existence via AUTHENTICATION_REQUIRED (see UsernameNotFoundException).
        provider.setHideUserNotFoundExceptions(true);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }
}
