package com.almanatura.api.exception;

/**
 * Signals a missing aggregate; mapped to {@link ErrorCode#RESOURCE_NOT_FOUND} when handled
 * centrally.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, Object id) {
        return new ResourceNotFoundException(resource + " not found with id: " + id);
    }
}
