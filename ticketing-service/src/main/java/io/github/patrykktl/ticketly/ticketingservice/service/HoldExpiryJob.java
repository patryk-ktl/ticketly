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

    @Scheduled(cron = "${ticketly.finish-events-cron}")
    @Transactional
    public void finishPastEvents() {
        LocalDateTime now = LocalDateTime.now();

        int eventsFinished = eventRepository.finishPastEvents(now);

        if (eventsFinished > 0) {
            log.info("Finish past events job completed: Finished {} events.",
                    eventsFinished);
        }
        log.info("Finish past events job was run.");
    }
}
