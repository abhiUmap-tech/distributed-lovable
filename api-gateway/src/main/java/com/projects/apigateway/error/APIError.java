package com.projects.apigateway.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

public record APIError(
        HttpStatus status,
        String message,
        Instant timestamp,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<APIFieldError> errors) {

    public APIError(HttpStatus status, String message) {
        this(status, message, Instant.now(), null
        );
    }

    public APIError(HttpStatus status, String message, List<APIFieldError> errors) {
        this(status, message, Instant.now(), errors
        );
    }
}

record APIFieldError(String field, String message){}
