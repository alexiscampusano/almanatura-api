package com.almanatura.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almanatura.api.enums.ApplicationStatus;
import com.almanatura.api.exception.InvalidApplicationTransitionException;

/**
 * Unit tests for {@link ApplicationStatusTransitions} business logic. Validates state machine rules
 * for application workflow.
 */
class ApplicationStatusTransitionsTest {

    @Test
    @DisplayName("SUBMITTED can transition to UNDER_REVIEW")
    void testSubmittedToUnderReview() {
        // Should not throw
        ApplicationStatusTransitions.requireTransition(
                ApplicationStatus.SUBMITTED, ApplicationStatus.UNDER_REVIEW);
    }

    @Test
    @DisplayName("SUBMITTED can transition to REJECTED")
    void testSubmittedToRejected() {
        ApplicationStatusTransitions.requireTransition(
                ApplicationStatus.SUBMITTED, ApplicationStatus.REJECTED);
    }

    @Test
    @DisplayName("SUBMITTED cannot transition to APPROVED (illegal)")
    void testSubmittedToApprovedFails() {
        assertThatThrownBy(
                        () ->
                                ApplicationStatusTransitions.requireTransition(
                                        ApplicationStatus.SUBMITTED, ApplicationStatus.APPROVED))
                .isInstanceOf(InvalidApplicationTransitionException.class);
    }

    @Test
    @DisplayName("UNDER_REVIEW can transition to REJECTED")
    void testUnderReviewToRejected() {
        ApplicationStatusTransitions.requireTransition(
                ApplicationStatus.UNDER_REVIEW, ApplicationStatus.REJECTED);
    }

    @Test
    @DisplayName("UNDER_REVIEW can transition to NEEDS_INFO")
    void testUnderReviewToNeedsInfo() {
        ApplicationStatusTransitions.requireTransition(
                ApplicationStatus.UNDER_REVIEW, ApplicationStatus.NEEDS_INFO);
    }

    @Test
    @DisplayName("UNDER_REVIEW can transition to APPROVED")
    void testUnderReviewToApproved() {
        ApplicationStatusTransitions.requireTransition(
                ApplicationStatus.UNDER_REVIEW, ApplicationStatus.APPROVED);
    }

    @Test
    @DisplayName("UNDER_REVIEW cannot transition to SUBMITTED (backward)")
    void testUnderReviewToSubmittedFails() {
        assertThatThrownBy(
                        () ->
                                ApplicationStatusTransitions.requireTransition(
                                        ApplicationStatus.UNDER_REVIEW,
                                        ApplicationStatus.SUBMITTED))
                .isInstanceOf(InvalidApplicationTransitionException.class);
    }

    @Test
    @DisplayName("NEEDS_INFO can transition to UNDER_REVIEW")
    void testNeedsInfoToUnderReview() {
        ApplicationStatusTransitions.requireTransition(
                ApplicationStatus.NEEDS_INFO, ApplicationStatus.UNDER_REVIEW);
    }

    @Test
    @DisplayName("NEEDS_INFO can transition to REJECTED")
    void testNeedsInfoToRejected() {
        ApplicationStatusTransitions.requireTransition(
                ApplicationStatus.NEEDS_INFO, ApplicationStatus.REJECTED);
    }

    @Test
    @DisplayName("NEEDS_INFO cannot transition to APPROVED (must go through UNDER_REVIEW)")
    void testNeedsInfoToApprovedFails() {
        assertThatThrownBy(
                        () ->
                                ApplicationStatusTransitions.requireTransition(
                                        ApplicationStatus.NEEDS_INFO, ApplicationStatus.APPROVED))
                .isInstanceOf(InvalidApplicationTransitionException.class);
    }

    @Test
    @DisplayName("APPROVED can only transition to REGISTERED_AS_ACTOR")
    void testApprovedToRegisteredAsActor() {
        ApplicationStatusTransitions.requireTransition(
                ApplicationStatus.APPROVED, ApplicationStatus.REGISTERED_AS_ACTOR);
    }

    @Test
    @DisplayName("APPROVED cannot transition to REJECTED (terminal state)")
    void testApprovedToRejectedFails() {
        assertThatThrownBy(
                        () ->
                                ApplicationStatusTransitions.requireTransition(
                                        ApplicationStatus.APPROVED, ApplicationStatus.REJECTED))
                .isInstanceOf(InvalidApplicationTransitionException.class);
    }

    @Test
    @DisplayName("REJECTED is terminal (no transitions allowed)")
    void testRejectedIsTerminal() {
        for (ApplicationStatus target : ApplicationStatus.values()) {
            if (target != ApplicationStatus.REJECTED) {
                assertThatThrownBy(
                                () ->
                                        ApplicationStatusTransitions.requireTransition(
                                                ApplicationStatus.REJECTED, target))
                        .isInstanceOf(InvalidApplicationTransitionException.class);
            }
        }
    }

    @Test
    @DisplayName("REGISTERED_AS_ACTOR is terminal (no transitions allowed)")
    void testRegisteredAsActorIsTerminal() {
        for (ApplicationStatus target : ApplicationStatus.values()) {
            if (target != ApplicationStatus.REGISTERED_AS_ACTOR) {
                assertThatThrownBy(
                                () ->
                                        ApplicationStatusTransitions.requireTransition(
                                                ApplicationStatus.REGISTERED_AS_ACTOR, target))
                        .isInstanceOf(InvalidApplicationTransitionException.class);
            }
        }
    }

    @Test
    @DisplayName("same status is allowed (idempotent)")
    void testSameStatusAllowed() {
        for (ApplicationStatus status : ApplicationStatus.values()) {
            // Should not throw
            ApplicationStatusTransitions.requireTransition(status, status);
        }
    }

    @Test
    @DisplayName("all ApplicationStatus enums are either in ALLOWED map or terminal")
    void testAllStatusesCovered() {
        Set<ApplicationStatus> mapped =
                Set.of(
                        ApplicationStatus.SUBMITTED,
                        ApplicationStatus.UNDER_REVIEW,
                        ApplicationStatus.NEEDS_INFO,
                        ApplicationStatus.APPROVED);
        Set<ApplicationStatus> terminal =
                Set.of(ApplicationStatus.REJECTED, ApplicationStatus.REGISTERED_AS_ACTOR);

        Set<ApplicationStatus> allStates = EnumSet.allOf(ApplicationStatus.class);
        Set<ApplicationStatus> covered = EnumSet.copyOf(mapped);
        covered.addAll(terminal);

        assertThat(covered).containsAll(allStates);
        assertThat(allStates).hasSameSizeAs(covered);
    }
}
