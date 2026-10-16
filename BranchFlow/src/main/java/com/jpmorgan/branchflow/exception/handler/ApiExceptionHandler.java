package com.jpmorgan.branchflow.exception.handler;


import com.jpmorgan.branchflow.exception.NoRouteFoundException;
import com.jpmorgan.branchflow.exception.UnknownBranchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(UnknownBranchException.class)
    public ResponseEntity<ApiErrorResponse> handleUnknownBranch(UnknownBranchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(NoRouteFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoRouteFound(NoRouteFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse("Invalid request parameters", errors));
    }

    public record ApiErrorResponse(String message, Map<String, String> errors) {
        public ApiErrorResponse(String message) {
            this(message, null);
        }
    }
}