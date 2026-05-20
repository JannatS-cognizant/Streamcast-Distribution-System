package com.cts.schedule_service.exception;

public class InvalidScheduleException extends RuntimeException {
    public InvalidScheduleException(String message) {
        super(message);
    }
}
