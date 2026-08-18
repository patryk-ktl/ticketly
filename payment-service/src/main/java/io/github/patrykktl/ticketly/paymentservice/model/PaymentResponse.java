package io.github.patrykktl.ticketly.paymentservice.model;

import lombok.Builder;

@Builder
public record PaymentResponse(
        Integer id,
        PaymentStatus status,
        String processedBy
) {
}
