package com.almanatura.api.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.CreateUserRequest;
import com.almanatura.api.dto.UserSummary;
import com.almanatura.api.service.AdminUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/** Admin REST API for internal user provisioning (super user only). */
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(
        name = "Users (admin)",
        description =
                "Create and list internal users. Restricted to users with the super_user role"
                        + " (JWT Bearer).")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create internal user",
            description =
                    "Creates a new super_user or event_manager account with a bcrypt password hash."
                            + " Returns 409 if the email is already registered (case-insensitive).")
    public UserSummary create(@Valid @RequestBody CreateUserRequest body) {
        return adminUserService.create(body);
    }

    @GetMapping
    @Operation(
            summary = "List internal users",
            description =
                    "Returns all internal users as safe projections (id, email, name, role), sorted"
                            + " by id. Pagination may be added later.")
    public List<UserSummary> list() {
        return adminUserService.listAll();
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete internal user",
            description = "Deletes an internal user by id. A user cannot delete themselves.")
    public void delete(@org.springframework.web.bind.annotation.PathVariable Long id) {
        adminUserService.delete(id);
    }
}
