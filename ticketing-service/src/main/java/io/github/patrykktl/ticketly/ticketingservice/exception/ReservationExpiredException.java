package io.github.patrykktl.ticketly.ticketingservice.exception;

public class ReservationExpiredException extends RuntimeException {

    public ReservationExpiredException(String message) {
        super(message);
    }
}
