package io.github.patrykktl.ticketly.ticketingservice.model;

import lombok.Builder;

@Builder
public record PaymentResponse(
        Integer id,
        PaymentStatus status,
        String processedBy
) {
}
