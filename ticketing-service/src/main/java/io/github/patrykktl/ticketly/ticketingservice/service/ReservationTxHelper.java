package io.github.patrykktl.ticketly.ticketingservice.service;

import dto.PaymentResponse;
import dto.PaymentStatus;
import exception.InterruptedPaymentException;
import io.github.patrykktl.ticketly.ticketingservice.exception.InvalidStatusException;
import io.github.patrykktl.ticketly.ticketingservice.exception.ReservationExpiredException;
import io.github.patrykktl.ticketly.ticketingservice.mapper.ReservationMapper;
import io.github.patrykktl.ticketly.ticketingservice.model.Reservation;
import io.github.patrykktl.ticketly.ticketingservice.model.ReservationStatus;
import io.github.patrykktl.ticketly.ticketingservice.model.dto.ReservationDto;
import io.github.patrykktl.ticketly.ticketingservice.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class ReservationTxHelper {

    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public Reservation getAndValidateForConfirmation(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation of given id cannot be found."));

        if (!reservation.getStatus().equals(ReservationStatus.PENDING_PAYMENT)) {
            throw new InvalidStatusException("Reservation cannot be confirmed.");
        }
        if (!reservation.getHoldExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ReservationExpiredException("Reservation has already expired.");
        }
        return reservation;
    }

    @Transactional
    public ReservationDto recordPaymentOutcome(Integer reservationId, PaymentResponse paymentResponse) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation of given id cannot be found."));

        if (paymentResponse.status() == PaymentStatus.SUCCEEDED) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            return ReservationMapper.mapToDto(reservation);
        } else {
            String holdTimeString = reservation.getHoldExpiresAt().format(DateTimeFormatter.ofPattern("HH:mm"));
            throw new InterruptedPaymentException(
                    "Payment declined, your hold remains until " + holdTimeString
            );
        }
    }
}
