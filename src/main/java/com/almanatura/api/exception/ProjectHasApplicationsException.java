package com.almanatura.api.exception;

public class ProjectHasApplicationsException extends RuntimeException {

    public ProjectHasApplicationsException(long projectId) {
        super("Project " + projectId + " still has applications and cannot be deleted.");
    }
}
