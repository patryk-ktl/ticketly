package io.github.patrykktl.ticketly.ticketingservice.exception;

public class NoAvailableSeatsException extends RuntimeException {

    public NoAvailableSeatsException(String message) {
        super(message);
    }
}
