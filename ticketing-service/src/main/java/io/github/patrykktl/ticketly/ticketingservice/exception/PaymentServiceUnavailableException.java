package io.github.patrykktl.ticketly.ticketingservice.exception;

public class PaymentServiceUnavailableException extends RuntimeException {

    public PaymentServiceUnavailableException(String message) {
        super(message);
    }
}
