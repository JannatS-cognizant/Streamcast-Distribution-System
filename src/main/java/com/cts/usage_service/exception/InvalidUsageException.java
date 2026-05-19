package com.cts.usage_service.exception;

public class InvalidUsageException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    public InvalidUsageException(String message) {
        super(message);
    }
}
