package com.almanatura.api.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.ApplicationSubmittedResponse;
import com.almanatura.api.dto.SubmitApplicationRequest;
import com.almanatura.api.service.ApplicationSubmissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Tag(
        name = "Applications (public)",
        description = "Anonymous project applications. No JWT.")
public class ApplicationController {

    private final ApplicationSubmissionService applicationSubmissionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirements
    @Operation(
            summary = "Submit an application",
            description =
                    "Target project must be PUBLISHED. DNI stored encrypted. 409 if the same email"
                            + " already applied to that project.")
    public ApplicationSubmittedResponse submit(@Valid @RequestBody SubmitApplicationRequest body) {
        return applicationSubmissionService.submit(body);
    }
}
