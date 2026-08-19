package io.github.patrykktl.ticketly.ticketingservice.exception.handling;

import exception.ExceptionDTO;
import exception.InterruptedPaymentException;
import exception.ValidationExceptionDTO;
import io.github.patrykktl.ticketly.ticketingservice.exception.InvalidStatusException;
import io.github.patrykktl.ticketly.ticketingservice.exception.NoAvailableSeatsException;
import io.github.patrykktl.ticketly.ticketingservice.exception.PaymentServiceUnavailableException;
import io.github.patrykktl.ticketly.ticketingservice.exception.ReservationExpiredException;
import io.github.patrykktl.ticketly.ticketingservice.exception.SeatLimitReachedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleEntityNotFoundException(EntityNotFoundException exception) {
        return new ExceptionDTO(exception.getMessage());
    }

    @ExceptionHandler({
            ReservationExpiredException.class,
            NoAvailableSeatsException.class,
            InvalidStatusException.class,
            SeatLimitReachedException.class,
            InterruptedPaymentException.class,
            PaymentServiceUnavailableException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDTO handleException(RuntimeException exception) {
        return new ExceptionDTO(exception.getMessage());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDTO handleOptimisticLockingFailureException() {
        return new ExceptionDTO("The resource was updated by another request. Please try again.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationExceptionDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ValidationExceptionDTO exceptionDTO = new ValidationExceptionDTO();
        exception.getFieldErrors().forEach(error ->
                exceptionDTO.addViolation(error.getField(), error.getDefaultMessage()));
        return exceptionDTO;
    }
}
