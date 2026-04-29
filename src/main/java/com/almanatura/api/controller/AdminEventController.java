package com.almanatura.api.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.AdminAttendeeResponse;
import com.almanatura.api.dto.CreateEventRequest;
import com.almanatura.api.dto.EventResponse;
import com.almanatura.api.dto.UpdateEventRequest;
import com.almanatura.api.service.AdminAttendeeService;
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
                "CRUD for cultural events and listing registrants per event. Requires an internal"
                        + " JWT (super_user or event_manager).")
public class AdminEventController {

    private final AdminEventService adminEventService;
    private final AdminAttendeeService adminAttendeeService;

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

    @GetMapping("/{id}/attendees")
    @Operation(
            summary = "List attendees for a cultural event",
            description =
                    "Returns registrants for the given event id (any status), sorted by"
                        + " registration time. Includes decrypted national ID — internal staff only"
                        + " (super_user or event_manager). Returns 404 if the event id does not"
                        + " exist.")
    public List<AdminAttendeeResponse> listAttendees(@PathVariable long id) {
        return adminAttendeeService.listForEvent(id);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get cultural event by id",
            description = "Returns 404 if the id does not exist.")
    public EventResponse getById(@PathVariable long id) {
        return adminEventService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Replace cultural event",
            description =
                    "Replaces title, description, schedule, location, capacity, and status"
                            + " (DRAFT, PUBLISHED, or CANCELLED). Returns 404 if the id does not"
                            + " exist.")
    public EventResponse update(
            @PathVariable long id, @Valid @RequestBody UpdateEventRequest body) {
        return adminEventService.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete cultural event",
            description = "Removes the event row. Returns 404 if the id does not exist.")
    public void delete(@PathVariable long id) {
        adminEventService.delete(id);
    }
}
