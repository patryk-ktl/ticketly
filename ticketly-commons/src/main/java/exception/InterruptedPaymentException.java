package exception;

public class InterruptedPaymentException extends RuntimeException {

    public InterruptedPaymentException(String message) {
        super(message);
    }
}
