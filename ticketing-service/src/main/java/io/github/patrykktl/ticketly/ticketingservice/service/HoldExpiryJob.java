package io.github.patrykktl.ticketly.ticketingservice.service;

import io.github.patrykktl.ticketly.ticketingservice.repository.EventRepository;
import io.github.patrykktl.ticketly.ticketingservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class HoldExpiryJob {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;

    @Scheduled(cron = "${ticketly.hold-cleanup-cron}")
    @Transactional
    public void releaseExpiredHolds() {
        LocalDateTime now = LocalDateTime.now();

        int seatsRestored = eventRepository.restoreSeatsWithExpiredReservations(now);
        int reservationsExpired = reservationRepository.expirePendingHolds(now);

        if (reservationsExpired > 0) {
            log.info("Hold expiry job completed: Expired {} reservations and restored {} seats to event inventory.",
                    reservationsExpired, seatsRestored);
        }
        log.info("Hold expiry job was run.");
    }
}
