package com.almanatura.api.exception;

/** Maps to {@link ErrorCode#PROJECT_HAS_APPLICATIONS} in problem responses. */
public class ProjectHasApplicationsException extends RuntimeException {

    public ProjectHasApplicationsException(long projectId) {
        super("Project " + projectId + " still has applications and cannot be deleted.");
    }
}
