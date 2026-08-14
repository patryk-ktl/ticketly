package io.github.patrykktl.ticketly.ticketingservice.integration;

import io.github.patrykktl.ticketly.ticketingservice.model.Concert;
import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import io.github.patrykktl.ticketly.ticketingservice.model.EventStatus;
import io.github.patrykktl.ticketly.ticketingservice.model.Reservation;
import io.github.patrykktl.ticketly.ticketingservice.model.ReservationStatus;
import io.github.patrykktl.ticketly.ticketingservice.repository.EventRepository;
import io.github.patrykktl.ticketly.ticketingservice.repository.ReservationRepository;
import io.github.patrykktl.ticketly.ticketingservice.service.HoldExpiryJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HoldExpiryJobIntegrationTest {

    @Autowired
    private HoldExpiryJob holdExpiryJob;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Event event;
    private Reservation expiredReservation;
    private Reservation freshReservation;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();

        Event unsavedEvent = Concert.builder()
                .title("Rock Concert")
                .city("Warsaw")
                .venue("National Stadium")
                .startsAt(LocalDateTime.now().plusDays(5))
                .basePrice(new BigDecimal("150.00"))
                .totalSeats(100)
                .availableSeats(10)
                .status(EventStatus.ON_SALE)
                .createdAt(LocalDateTime.now())
                .artist("The Tester")
                .genre("Test Music")
                .supportAct("Mockito")
                .build();

        event = eventRepository.saveAndFlush(unsavedEvent);

        expiredReservation = Reservation.builder()
                .event(event)
                .customerEmail("test1@test.com")
                .seats(3)
                .totalPrice(BigDecimal.valueOf(300))
                .status(ReservationStatus.PENDING_PAYMENT)
                .holdExpiresAt(LocalDateTime.now().minusMinutes(5))
                .createdAt(LocalDateTime.now().minusDays(7))
                .build();

        freshReservation = Reservation.builder()
                .event(event)
                .customerEmail("test1@test.com")
                .seats(2)
                .totalPrice(BigDecimal.valueOf(200))
                .status(ReservationStatus.PENDING_PAYMENT)
                .holdExpiresAt(LocalDateTime.now().plusMinutes(10))
                .createdAt(LocalDateTime.now().minusDays(7))
                .build();

        reservationRepository.saveAndFlush(expiredReservation);
        reservationRepository.saveAndFlush(freshReservation);
    }

    @Test
    void testReleaseExpiredHolds_ShouldProcessOnlyExpiredReservations() {
        holdExpiryJob.releaseExpiredHolds();

        Reservation updatedExpired = reservationRepository.findById(expiredReservation.getId()).orElseThrow();
        Reservation updatedFresh = reservationRepository.findById(freshReservation.getId()).orElseThrow();
        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();

        assertThat(updatedExpired.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(updatedFresh.getStatus()).isEqualTo(ReservationStatus.PENDING_PAYMENT);
        assertThat(updatedEvent.getAvailableSeats()).isEqualTo(13);
    }
}
