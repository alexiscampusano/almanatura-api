package com.almanatura.api.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.CreateEventRequest;
import com.almanatura.api.dto.EventResponse;
import com.almanatura.api.service.AdminEventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Tag(
        name = "Events (admin)",
        description =
                "Create and read cultural events. Requires an internal JWT (super_user or"
                        + " event_manager). Update and delete are planned for a follow-up task.")
public class AdminEventController {

    private final AdminEventService adminEventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create cultural event",
            description =
                    "Creates a new event in DRAFT status. Publishing for the public agenda will be"
                            + " added later.")
    public EventResponse create(@Valid @RequestBody CreateEventRequest body) {
        return adminEventService.create(body);
    }

    @GetMapping
    @Operation(
            summary = "List cultural events",
            description =
                    "Returns all events sorted by start time (ascending). Pagination may be added"
                            + " later.")
    public List<EventResponse> list() {
        return adminEventService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get cultural event by id",
            description = "Returns 404 if the id does not exist.")
    public EventResponse getById(@PathVariable long id) {
        return adminEventService.getById(id);
    }
}
