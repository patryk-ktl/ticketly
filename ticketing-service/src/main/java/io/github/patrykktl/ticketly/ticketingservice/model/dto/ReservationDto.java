package io.github.patrykktl.ticketly.ticketingservice.model.dto;

import io.github.patrykktl.ticketly.ticketingservice.model.ReservationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class ReservationDto {

    private Integer id;
    private Integer eventId;
    private String customerEmail;
    private Integer seats;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private LocalDateTime holdExpiresAt;
    private LocalDateTime createdAt;
}
