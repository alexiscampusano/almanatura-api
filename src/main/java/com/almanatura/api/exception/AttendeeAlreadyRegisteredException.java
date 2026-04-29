package com.almanatura.api.exception;

/** Thrown when the same email is registered twice for the same cultural event. */
public class AttendeeAlreadyRegisteredException extends RuntimeException {

    public AttendeeAlreadyRegisteredException() {
        super("An attendee with this email is already registered for this event.");
    }
}
