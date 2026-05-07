package com.almanatura.api.service;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.almanatura.api.enums.ApplicationStatus;
import com.almanatura.api.exception.InvalidApplicationTransitionException;

public final class ApplicationStatusTransitions {

    private static final Map<ApplicationStatus, Set<ApplicationStatus>> ALLOWED =
            Map.ofEntries(
                    Map.entry(
                            ApplicationStatus.SUBMITTED,
                            EnumSet.of(ApplicationStatus.UNDER_REVIEW, ApplicationStatus.REJECTED)),
                    Map.entry(
                            ApplicationStatus.UNDER_REVIEW,
                            EnumSet.of(
                                    ApplicationStatus.REJECTED,
                                    ApplicationStatus.NEEDS_INFO,
                                    ApplicationStatus.APPROVED)),
                    Map.entry(
                            ApplicationStatus.NEEDS_INFO,
                            EnumSet.of(ApplicationStatus.UNDER_REVIEW, ApplicationStatus.REJECTED)),
                    Map.entry(
                            ApplicationStatus.APPROVED,
                            EnumSet.of(ApplicationStatus.REGISTERED_AS_ACTOR)));

    private ApplicationStatusTransitions() {}

    public static void requireTransition(ApplicationStatus from, ApplicationStatus to) {
        if (from == to) {
            return;
        }
        Set<ApplicationStatus> next = ALLOWED.get(from);
        if (next == null || !next.contains(to)) {
            throw new InvalidApplicationTransitionException(from, to);
        }
    }
}
