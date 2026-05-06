package com.almanatura.api.exception;

public class ApplicationAlreadyExistsException extends RuntimeException {

    public ApplicationAlreadyExistsException() {
        super("An application with this email already exists for this project.");
    }
}
