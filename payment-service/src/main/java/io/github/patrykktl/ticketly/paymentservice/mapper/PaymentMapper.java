package io.github.patrykktl.ticketly.paymentservice.mapper;

import io.github.patrykktl.ticketly.paymentservice.model.Payment;
import io.github.patrykktl.ticketly.paymentservice.model.PaymentResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PaymentMapper {

    public PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .status(payment.getStatus())
                .processedBy(payment.getProcessedBy())
                .build();
    }
}
