package io.github.patrykktl.ticketly.paymentservice.mapper;

import dto.PaymentResponse;
import io.github.patrykktl.ticketly.paymentservice.model.Payment;
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
