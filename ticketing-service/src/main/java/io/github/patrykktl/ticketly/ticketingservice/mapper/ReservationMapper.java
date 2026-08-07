package io.github.patrykktl.ticketly.ticketingservice.mapper;

import io.github.patrykktl.ticketly.ticketingservice.model.Reservation;
import io.github.patrykktl.ticketly.ticketingservice.model.dto.ReservationDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReservationMapper {

    public ReservationDto mapToDto(Reservation reservation) {
        return ReservationDto.builder()
                .id(reservation.getId())
                .eventId(reservation.getEvent().getId())
                .customerEmail(reservation.getCustomerEmail())
                .seats(reservation.getSeats())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .holdExpiresAt(reservation.getHoldExpiresAt())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
