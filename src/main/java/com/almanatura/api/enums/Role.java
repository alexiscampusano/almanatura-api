package com.almanatura.api.enums;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Internal roles for the AlmaNatura platform. Authority strings keep the {@code ROLE_} prefix
 * expected by Spring Security.
 */
public enum Role {
    SUPER_USER,
    EVENT_MANAGER;

    public String authority() {
        return "ROLE_" + name();
    }

    public List<GrantedAuthority> grantedAuthorities() {
        return List.of(new SimpleGrantedAuthority(authority()));
    }
}
