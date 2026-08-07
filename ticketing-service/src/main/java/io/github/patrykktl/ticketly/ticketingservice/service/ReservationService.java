package io.github.patrykktl.ticketly.ticketingservice.service;

import io.github.patrykktl.ticketly.ticketingservice.exception.InvalidStatusException;
import io.github.patrykktl.ticketly.ticketingservice.exception.NoAvailableSeatsException;
import io.github.patrykktl.ticketly.ticketingservice.exception.ReservationExpiredException;
import io.github.patrykktl.ticketly.ticketingservice.mapper.ReservationMapper;
import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import io.github.patrykktl.ticketly.ticketingservice.model.EventStatus;
import io.github.patrykktl.ticketly.ticketingservice.model.Reservation;
import io.github.patrykktl.ticketly.ticketingservice.model.ReservationStatus;
import io.github.patrykktl.ticketly.ticketingservice.model.command.CreateReservationCommand;
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
    public ReservationDto createReservation(CreateReservationCommand command) {
        Event event = eventRepository.findById(command.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event of given id cannot be found."));
        Integer availableSeats = event.getAvailableSeats();
        Integer seats = command.getSeats();

        if (!event.getStatus().equals(EventStatus.ON_SALE)) {
            throw new InvalidStatusException("Reservations for the selected event are not on sale.");
        }
        if (availableSeats < seats) {
            throw new NoAvailableSeatsException("There are no seats available for the selected event");
        }

        event.setAvailableSeats(availableSeats - seats);
        BigDecimal totalPrice = event.getBasePrice().multiply(BigDecimal.valueOf(seats));

        Reservation reservation = Reservation.builder()
                .event(event)
                .customerEmail(command.getCustomerEmail())
                .seats(seats)
                .totalPrice(totalPrice)
                .status(ReservationStatus.PENDING_PAYMENT)
                .holdExpiresAt(LocalDateTime.now().minusMinutes(15))
                .createdAt(LocalDateTime.now())
                .build();

        return ReservationMapper.mapToDto(reservationRepository.save(reservation));
    }

    // M11: payment happens here
    @Transactional
    public ReservationDto confirm(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation of given id cannot be found."));

        if (!reservation.getStatus().equals(ReservationStatus.PENDING_PAYMENT)) {
            throw new InvalidStatusException("Reservation cannot be confirmed.");
        }
        if (!reservation.getHoldExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ReservationExpiredException("Reservation has already expired.");
        }
        reservation.setStatus(ReservationStatus.CONFIRMED);
        return ReservationMapper.mapToDto(reservation);
    }

    public ReservationDto findById(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation of given id cannot be found."));
        return ReservationMapper.mapToDto(reservation);
    }
}
