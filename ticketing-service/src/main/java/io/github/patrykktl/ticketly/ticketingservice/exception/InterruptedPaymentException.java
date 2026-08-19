package io.github.patrykktl.ticketly.ticketingservice.exception;

public class InterruptedPaymentException extends RuntimeException {

    public InterruptedPaymentException(String message) {
        super(message);
    }
}
