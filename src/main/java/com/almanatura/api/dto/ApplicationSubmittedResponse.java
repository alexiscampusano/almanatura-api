package com.almanatura.api.dto;

import java.time.Instant;

public record ApplicationSubmittedResponse(Long id, Long projectId, Instant submittedAt) {}
