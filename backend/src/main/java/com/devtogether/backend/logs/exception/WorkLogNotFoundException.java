package com.devtogether.backend.logs.exception;

public class WorkLogNotFoundException extends RuntimeException {

    public WorkLogNotFoundException(Long id) {
        super("Work log not found with ID: " + id);
    }

    public WorkLogNotFoundException(String message) {
        super(message);
    }
}
