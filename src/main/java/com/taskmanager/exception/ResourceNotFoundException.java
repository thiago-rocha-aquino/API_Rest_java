package com.taskmanager.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s não encontrado com id: %d", resourceName, id));
    }

    public ResourceNotFoundException(String resourceName, String field, String value) {
        super(String.format("%s não encontrado com %s: %s", resourceName, field, value));
    }
}
