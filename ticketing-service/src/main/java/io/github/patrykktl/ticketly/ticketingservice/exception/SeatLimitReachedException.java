package io.github.patrykktl.ticketly.ticketingservice.exception;

public class SeatLimitReachedException extends RuntimeException {

    public SeatLimitReachedException(String message) {
        super(message);
    }
}
