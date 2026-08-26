package io.github.patrykktl.ticketly.paymentservice.exception.handling;

import exception.ExceptionDTO;
import exception.InterruptedPaymentException;
import exception.ValidationExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            InterruptedPaymentException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDTO handleException(RuntimeException exception) {
        return new ExceptionDTO(exception.getMessage());
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
