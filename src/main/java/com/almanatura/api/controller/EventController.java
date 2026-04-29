package com.almanatura.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.PublicEventResponse;
import com.almanatura.api.service.PublicEventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(
        name = "Events (public)",
        description = "Read-only agenda of published cultural events (no JWT required).")
public class EventController {

    private final PublicEventService publicEventService;

    @GetMapping
    @SecurityRequirements
    @Operation(
            summary = "List published events",
            description =
                    "Returns events with status PUBLISHED only, sorted by start time (ascending)."
                            + " Pagination may be added later.")
    public List<PublicEventResponse> list() {
        return publicEventService.listPublished();
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(
            summary = "Get published event by id",
            description =
                    "Returns 404 RESOURCE_NOT_FOUND if the id does not exist or the event is not"
                            + " PUBLISHED (e.g. DRAFT or CANCELLED).")
    public PublicEventResponse getById(@PathVariable long id) {
        return publicEventService.getPublished(id);
    }
}
