package io.github.patrykktl.ticketly.ticketingservice.client;

import dto.PaymentRequest;
import dto.PaymentResponse;
import io.github.patrykktl.ticketly.ticketingservice.exception.PaymentServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentClientFallback implements PaymentClient {

    @Override
    public PaymentResponse charge(PaymentRequest request) {
        log.warn("Payment service unavailable, falling back for reservation {}", request.reservationId());
        throw new PaymentServiceUnavailableException("payments temporarily unavailable");
    }
}
