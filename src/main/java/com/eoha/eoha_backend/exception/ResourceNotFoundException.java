package com.eoha.eoha_backend.exception;

/**
 * Thrown by the service layer when a requested entity does not exist. Mapped to
 * HTTP 404 by {@link GlobalExceptionHandler}. Other {@link RuntimeException} types are
 * treated as unexpected server errors (HTTP 500).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
