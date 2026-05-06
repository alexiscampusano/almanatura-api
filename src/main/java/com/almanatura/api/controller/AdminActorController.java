package com.almanatura.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.PublicActorResponse;
import com.almanatura.api.service.AdminActorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/actors")
@RequiredArgsConstructor
@Tag(name = "Actors (admin)", description = "Internal actor directory. JWT required.")
public class AdminActorController {

    private final AdminActorService adminActorService;

    @GetMapping
    @Operation(summary = "List all actors")
    public List<PublicActorResponse> list() {
        return adminActorService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get actor by id")
    public PublicActorResponse getById(@PathVariable long id) {
        return adminActorService.getById(id);
    }
}
