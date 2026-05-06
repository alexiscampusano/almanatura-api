package com.almanatura.api.enums;

/**
 * Application workflow for a {@link com.almanatura.api.entity.ProjectApplication}. Terminal states:
 * {@code REJECTED}, {@code REGISTERED_AS_ACTOR}.
 */
public enum ApplicationStatus {
    SUBMITTED,
    UNDER_REVIEW,
    REJECTED,
    NEEDS_INFO,
    APPROVED,
    REGISTERED_AS_ACTOR
}
