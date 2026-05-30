package com.projects.commonlib.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<APIError> handleBadRequestException(BadRequestException badRequestException){
        var apiError = new APIError(HttpStatus.BAD_GATEWAY, badRequestException.getMessage());
        log.error(apiError.toString(), badRequestException);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIError> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException){
        var apiError = new APIError(HttpStatus.NOT_FOUND, resourceNotFoundException.getResourceName() + " with id " + resourceNotFoundException.getResourceId() + " not found");
        log.error(apiError.toString(), resourceNotFoundException);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIError> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException){

        var fieldErrors = methodArgumentNotValidException.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new APIFieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        var apiError = new APIError(HttpStatus.BAD_REQUEST, "Input Validation Failed", fieldErrors);
        log.error(apiError.toString(), methodArgumentNotValidException);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIError> handleRuntimeException(RuntimeException ex) {
        var apiError = new APIError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }
}
