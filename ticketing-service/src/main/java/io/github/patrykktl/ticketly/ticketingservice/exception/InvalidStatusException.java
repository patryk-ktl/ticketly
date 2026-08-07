package io.github.patrykktl.ticketly.ticketingservice.exception;

public class InvalidStatusException extends RuntimeException {

    public InvalidStatusException(String message) {
        super(message);
    }
}
