package com.almanatura.api.exception;

/** Maps to {@link ErrorCode#PARTICIPATION_ALREADY_EXISTS} in problem responses. */
public class ParticipationAlreadyExistsException extends RuntimeException {

    public ParticipationAlreadyExistsException() {
        super("A participation already exists for this activity and actor.");
    }
}
