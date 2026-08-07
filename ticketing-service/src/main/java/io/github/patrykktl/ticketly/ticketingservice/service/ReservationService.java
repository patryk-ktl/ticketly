package io.github.patrykktl.ticketly.ticketingservice.service;

import io.github.patrykktl.ticketly.ticketingservice.mapper.ReservationMapper;
import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import io.github.patrykktl.ticketly.ticketingservice.model.EventStatus;
import io.github.patrykktl.ticketly.ticketingservice.model.Reservation;
import io.github.patrykktl.ticketly.ticketingservice.model.ReservationStatus;
import io.github.patrykktl.ticketly.ticketingservice.model.dto.ReservationDto;
import io.github.patrykktl.ticketly.ticketingservice.repository.EventRepository;
import io.github.patrykktl.ticketly.ticketingservice.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;

    @Transactional
    public ReservationDto createReservation(Integer eventId, String customerEmail, Integer seats) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EntityNotFoundException::new);
        Integer availableSeats = event.getAvailableSeats();

        if (!event.getStatus().equals(EventStatus.ON_SALE) ||
                availableSeats < seats) {
            throw new IllegalArgumentException("A reservation for the selected event cannot be made.");
        }

        event.setAvailableSeats(availableSeats - seats);
        BigDecimal totalPrice = event.getBasePrice().multiply(BigDecimal.valueOf(seats));

        Reservation reservation = Reservation.builder()
                .event(event)
                .customerEmail(customerEmail)
                .seats(seats)
                .totalPrice(totalPrice)
                .status(ReservationStatus.PENDING_PAYMENT)
                .holdExpiresAt(LocalDateTime.now().minusMinutes(15))
                .createdAt(LocalDateTime.now())
                .build();

        return ReservationMapper.mapToDto(reservationRepository.save(reservation));
    }
}
