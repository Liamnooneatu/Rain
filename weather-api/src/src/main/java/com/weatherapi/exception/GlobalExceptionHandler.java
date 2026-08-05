/*
 * This class provides centralized exception handling for the weather API
 * using Spring Boot's @RestControllerAdvice annotation.
 *
 * It captures exceptions thrown throughout the application and converts
 * them into consistent HTTP error responses using the ErrorResponse DTO.
 *
 * Handled exceptions include:
 * - MethodArgumentNotValidException:
 *   Handles validation failures for invalid request data.
 *
 * - InvalidQueryException:
 *   Handles invalid metric or statistic query parameters.
 *
 * - MissingServletRequestParameterException:
 *   Handles missing required API request parameters.
 *
 * - MethodArgumentTypeMismatchException:
 *   Handles incorrect parameter data types.
 *
 * - Exception:
 *   Handles unexpected server-side errors.
 *
 * Each exception handler returns an appropriate HTTP status code and a
 * descriptive error message, improving API usability and making errors
 * easier for clients to understand and debug.
 */


package com.weatherapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return build(HttpStatus.BAD_REQUEST, messages);
    }


    @ExceptionHandler(InvalidQueryException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQuery(InvalidQueryException ex) {
        return build(HttpStatus.BAD_REQUEST, List.of(ex.getMessage()));
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, List.of("Missing required parameter: " + ex.getParameterName()));
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST,
                List.of("Invalid value for parameter '" + ex.getName() + "': " + ex.getValue()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, List.of("Unexpected error: " + ex.getMessage()));
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, List<String> messages) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), status.getReasonPhrase(), messages));
    }
}
