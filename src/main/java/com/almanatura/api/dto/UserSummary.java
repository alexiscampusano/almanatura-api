package com.almanatura.api.dto;

import com.almanatura.api.enums.Role;

/**
 * Public projection of a {@code User} that is safe to return to the client. Excludes the password
 * hash and audit metadata on purpose.
 */
public record UserSummary(Long id, String email, String name, Role role) {}
