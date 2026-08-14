package io.github.patrykktl.ticketly.ticketingservice.model.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record EventCardDto(
        Integer id,
        String title,
        String city,
        LocalDateTime startsAt,
        BigDecimal basePrice,
        Integer availableSeats
) {
}
