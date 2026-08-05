/*
 * This custom exception represents errors caused by invalid API query parameters or unsupported request values.
 *
 * It extends RuntimeException, allowing the exception to be thrown without
 * requiring explicit handling throughout the application.
 *
 * This exception is used when user input fails validation, such as:
 * - Providing an unsupported weather metric.
 * - Providing an invalid statistic type.
 * - Supplying missing or blank query values.
 *
 * The GlobalExceptionHandler catches this exception and converts it into
 * a structured HTTP error response for the API client.
 */

package com.weatherapi.exception;

public class InvalidQueryException extends RuntimeException {
    public InvalidQueryException(String message) {
        super(message);
    }
}
