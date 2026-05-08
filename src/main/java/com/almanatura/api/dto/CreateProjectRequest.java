package com.almanatura.api.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.almanatura.api.enums.ProjectPillar;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateProjectRequest(
        @NotBlank @Size(max = 255) @Schema(example = "Taller de apicultura rural") String title,
        @Size(max = 10_000)
                @Schema(
                        example =
                                "Curso de habilidades rurales orientado a jóvenes emprendedores del"
                                        + " medio rural")
                String description,
        @NotNull @Schema(example = "TECHNOLOGY") ProjectPillar pillar,
        @Schema(example = "2030-08-20T16:00:00Z") Instant startsAt,
        @Schema(example = "2030-08-20T17:30:00Z") Instant endsAt,
        @Size(max = 255) @Schema(example = "Casa de cultura, Villanueva de los Castillejos")
                String location,
        @Size(max = 512) @Schema(example = "https://example.com/project-image.jpg") String imageUrl) {}
