package com.almanatura.api.dto;

import com.almanatura.api.enums.EventStatus;

/** Number of cultural events in a given lifecycle state (reports summary). */
public record EventStatusCount(EventStatus status, long count) {}
