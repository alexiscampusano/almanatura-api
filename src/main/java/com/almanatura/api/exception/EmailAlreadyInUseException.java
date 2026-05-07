package com.almanatura.api.exception;

/**
 * Thrown when creating or updating a user would violate the unique email constraint; maps to {@link
 * ErrorCode#EMAIL_ALREADY_IN_USE}.
 */
public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException(String email) {
        super("An account with this email already exists: " + email);
    }
}
