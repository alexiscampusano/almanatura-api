package com.almanatura.api.exception;

/** Maps to {@link ErrorCode#APPLICATION_ALREADY_EXISTS} in problem responses. */
public class ApplicationAlreadyExistsException extends RuntimeException {

    public ApplicationAlreadyExistsException() {
        super("An application with this email already exists for this project.");
    }
}
