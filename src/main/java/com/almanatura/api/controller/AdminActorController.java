package com.almanatura.api.controller;

import java.util.List;

import jakarta.validation.constraints.Positive;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.PublicActorResponse;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.service.AdminActorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/** Internal REST endpoints for the actor directory. */
@RestController
@Validated
@RequestMapping("/admin/actors")
@RequiredArgsConstructor
@Tag(name = "Actors (admin)", description = "Internal actor directory. JWT required.")
public class AdminActorController {

    private final AdminActorService adminActorService;

    @GetMapping
    @Operation(summary = "List all actors", description = "Optional pillar filter.")
    public List<PublicActorResponse> list(
            @RequestParam(name = "pillar", required = false) ProjectPillar pillar) {
        return adminActorService.findAll(pillar);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get actor by id")
    public PublicActorResponse getById(@PathVariable @Positive long id) {
        return adminActorService.getById(id);
    }
}
