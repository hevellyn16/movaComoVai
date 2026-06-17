package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.error.CustomError;
import com.eng.software.mova.shared.exceptions.ApiException;
import com.eng.software.mova.shared.exceptions.ResourceAlreadyExistsException;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException e, WebRequest request){
        CustomError<String> error = new CustomError<>(e.getMessage(),
                request.getDescription(false), e.getStatus());

        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        CustomError<String> error = new CustomError<>(
                e.getMessage(),
                request.getDescription(false),
                HttpStatus.NOT_FOUND
        );

        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Object> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e, WebRequest request) {
        CustomError<String> error = new CustomError<>(
                e.getMessage(),
                request.getDescription(false),
                HttpStatus.CONFLICT
        );

        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e, WebRequest request) {
        List<String> validationErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> "message: " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        CustomError<List<String>> error = new CustomError<>(
                validationErrors,
                request.getDescription(false),
                HttpStatus.BAD_REQUEST
        );

        return new ResponseEntity<>(error, error.getStatus());
    }
}

