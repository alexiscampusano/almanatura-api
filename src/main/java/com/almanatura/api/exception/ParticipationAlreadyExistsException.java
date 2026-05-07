package com.almanatura.api.exception;

public class ParticipationAlreadyExistsException extends RuntimeException {

    public ParticipationAlreadyExistsException() {
        super("A participation already exists for this activity and actor.");
    }
}
