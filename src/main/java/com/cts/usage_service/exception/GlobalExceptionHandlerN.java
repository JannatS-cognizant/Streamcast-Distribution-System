package com.cts.usage_service.exception;

import com.cts.usage_service.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandlerN {

    // ✅ Validation errors (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        return new ApiResponse<>(
                false,
                "Validation failed",
                errors
        );
    }

    // ✅ Resource not found (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNotFound(ResourceNotFoundException ex) {
        return new ApiResponse<>(
                false,
                ex.getMessage(),
                null
        );
    }

    // ✅ Invalid input (400)
    @ExceptionHandler(InvalidUsageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleInvalidUsage(InvalidUsageException ex) {
        return new ApiResponse<>(
                false,
                ex.getMessage(),
                null
        );
    }

    // ✅ Generic fallback (500)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneral(Exception ex) {
        return new ApiResponse<>(
                false,
                "Something went wrong",
                null
        );
    }
}

