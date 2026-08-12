package io.github.patrykktl.ticketly.ticketingservice.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class EventCardDto {
    private Integer id;
    private String title;
    private String city;
    private LocalDateTime startsAt;
    private BigDecimal basePrice;
    private Integer availableSeats;
}
