package com.almanatura.api.exception;

/**
 * Thrown when creating or updating a user would violate the unique email constraint; maps to {@link
 * ErrorCode#EMAIL_ALREADY_IN_USE}.
 */
public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException(@SuppressWarnings("unused") String email) {
        super("Email already in use");
    }
}
