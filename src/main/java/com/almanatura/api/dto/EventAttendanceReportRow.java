package com.almanatura.api.dto;

import java.time.Instant;

import com.almanatura.api.enums.EventStatus;

/** Per-event registration count for admin reporting (no attendee PII). */
public record EventAttendanceReportRow(
        Long id, String title, Instant startsAt, EventStatus status, long attendeeCount) {}
