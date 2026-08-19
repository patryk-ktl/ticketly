package dto;

import lombok.Builder;

@Builder
public record PaymentResponse(
        Integer id,
        PaymentStatus status,
        String processedBy
) {
}
