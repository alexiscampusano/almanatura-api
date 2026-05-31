package com.almanatura.api.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.CreateUserRequest;
import com.almanatura.api.dto.UserSummary;
import com.almanatura.api.entity.User;
import com.almanatura.api.exception.EmailAlreadyInUseException;
import com.almanatura.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/** Creates internal {@link com.almanatura.api.entity.User} rows with encoded passwords. */
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserSummary create(CreateUserRequest request) {
        String email = request.email().trim();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyInUseException(email);
        }
        boolean enabled = request.enabled() == null || request.enabled();
        User user =
                User.builder()
                        .name(request.name().trim())
                        .email(email)
                        .passwordHash(passwordEncoder.encode(request.password()))
                        .role(request.role())
                        .enabled(enabled)
                        .build();
        User saved = userRepository.save(user);
        return toSummary(saved);
    }

    @Transactional(readOnly = true)
    public List<UserSummary> listAll() {
        return userRepository.findAllByOrderByIdAsc().stream()
                .map(AdminUserService::toSummary)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        com.almanatura.api.exception.ResourceNotFoundException.of(
                                                "User", id));
        String currentUserEmail =
                org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName();
        if (user.getEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new IllegalStateException("Cannot delete yourself");
        }
        userRepository.delete(user);
    }

    private static UserSummary toSummary(User user) {
        return new UserSummary(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
