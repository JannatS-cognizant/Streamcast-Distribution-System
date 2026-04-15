package com.Cts.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandlerN {

    //logic for the validation with respect to methods (get, put, post, delete)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message" , "Validation Failed");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        response.put("errors", errors);
        return response;

    }

    //logic for the exception when the resource is not found
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public Map<String,String> handleNotFound(ResourceNotFoundException ex){
        return Map.of("message", ex.getMessage());


    }

    //logic for the exception if there is any wrong input given while the creating or updating the schedule
    @ExceptionHandler(InvalidScheduleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public Map<String,String> handleInvalid(InvalidScheduleException ex){
        return Map.of("message", ex.getMessage());
    }

    //logic for the general exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500
    public Map<String,String> handleGeneral(Exception ex){
        return Map.of("message", "Something went wrong");
    }
}

