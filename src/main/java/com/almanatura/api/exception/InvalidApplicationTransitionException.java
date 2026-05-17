package com.almanatura.api.exception;

import com.almanatura.api.enums.ApplicationStatus;

/** Maps to {@link ErrorCode#INVALID_APPLICATION_TRANSITION} in problem responses. */
public class InvalidApplicationTransitionException extends RuntimeException {

    public InvalidApplicationTransitionException(ApplicationStatus from, ApplicationStatus to) {
        super("Cannot transition application from " + from + " to " + to + ".");
    }
}
