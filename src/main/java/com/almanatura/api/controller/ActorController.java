package com.almanatura.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.PublicActorResponse;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.service.PublicActorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/actors")
@RequiredArgsConstructor
@Tag(
        name = "Actors (public)",
        description =
                "Rural actor directory entries linked to REGISTERED_AS_ACTOR applications on"
                        + " PUBLISHED projects. No PII. No JWT.")
public class ActorController {

    private final PublicActorService publicActorService;

    @GetMapping
    @SecurityRequirements
    @Operation(
            summary = "List actors in the public directory",
            description =
                    "Optional pillar filter; actors appear when registered on a matching published"
                            + " project.")
    public List<PublicActorResponse> list(
            @RequestParam(name = "pillar", required = false) ProjectPillar pillar) {
        return publicActorService.listDirectory(pillar);
    }
}
