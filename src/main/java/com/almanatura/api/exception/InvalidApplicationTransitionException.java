package com.almanatura.api.exception;

import com.almanatura.api.enums.ApplicationStatus;

public class InvalidApplicationTransitionException extends RuntimeException {

    public InvalidApplicationTransitionException(ApplicationStatus from, ApplicationStatus to) {
        super("Cannot transition application from " + from + " to " + to + ".");
    }
}
