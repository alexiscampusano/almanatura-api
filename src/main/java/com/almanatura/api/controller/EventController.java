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

import com.almanatura.api.dto.PublicEventResponse;
import com.almanatura.api.dto.RegisterAttendeeRequest;
import com.almanatura.api.dto.RegistrationResponse;
import com.almanatura.api.service.EventRegistrationService;
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
        description = "Published cultural events agenda and public registration (no JWT required).")
public class EventController {

    private final PublicEventService publicEventService;
    private final EventRegistrationService eventRegistrationService;

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

    @PostMapping("/{id}/register")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirements
    @Operation(
            summary = "Register for a published event",
            description =
                    "Creates an attendee row with encrypted DNI. Returns 404 if the event does not"
                            + " exist or is not PUBLISHED; 409 EVENT_AT_CAPACITY when maxAttendees is"
                            + " reached; 409 ATTENDEE_ALREADY_REGISTERED when the same email is used"
                            + " twice for the same event.")
    public RegistrationResponse register(
            @PathVariable long id, @Valid @RequestBody RegisterAttendeeRequest body) {
        return eventRegistrationService.register(id, body);
    }
}
