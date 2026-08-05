/*
 * represents the structure of error
 * responses returned by the weather API when an exception occurs.
 *
 * stores:
 * - timestamp: The time when the error response was generated.
 * - status: The HTTP status code associated with the error.
 * - error: A short description of the error type.
 * - messages: A list of detailed error messages explaining what went
 *   wrong.
 *
 * This class provides a consistent format for returning errors to API
 * clients, making it easier to understand and handle validation failures,
 * invalid queries, and other application exceptions.
 */


package com.weatherapi.exception;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {
    private final Instant timestamp = Instant.now();
    private final int status;
    private final String error;
    private final List<String> messages;

    public ErrorResponse(int status, String error, List<String> messages) {
        this.status = status;
        this.error = error;
        this.messages = messages;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public List<String> getMessages() {
        return messages;
    }
}
