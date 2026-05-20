package com.cts.usage_service.exception;

public class InvalidUsageException extends RuntimeException {
    public InvalidUsageException(String message) {
        super(message);
    }
}
