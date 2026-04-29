package com.almanatura.api.exception;

/** Thrown when the event's {@code maxAttendees} capacity has been reached. */
public class EventAtCapacityException extends RuntimeException {

    public EventAtCapacityException() {
        super("This event has reached its maximum capacity.");
    }
}
