package com.cts.StreamCast.Exception;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)

	public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {



	Map<String, String> errors = new HashMap<>();



	ex.getBindingResult().getFieldErrors().forEach(error ->

	errors.put(error.getField(), error.getDefaultMessage())

	);



	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
}
	@ExceptionHandler(DuplicateTitleException.class)

	public ResponseEntity<String> handleDuplicate(DuplicateTitleException ex) {

		return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);

	}

	@ExceptionHandler(AssetNotFoundException.class)
	public ResponseEntity<String> handleAssetNotFound(AssetNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}


	@ExceptionHandler(TitlenotFoundException.class)
	public ResponseEntity<String> handleTitleNotFound(TitlenotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(ex.getMessage());
	}





}
