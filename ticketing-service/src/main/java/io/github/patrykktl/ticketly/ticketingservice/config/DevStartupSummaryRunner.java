package io.github.patrykktl.ticketly.ticketingservice.config;

import io.github.patrykktl.ticketly.ticketingservice.repository.EventRepository;
import io.github.patrykktl.ticketly.ticketingservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevStartupSummaryRunner implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final Environment environment;

    @Override
    public void run(String... args) {
        long eventCount = eventRepository.count();
        long reservationCount = reservationRepository.count();
        String activeProfiles = Arrays.toString(environment.getActiveProfiles());

        log.info("""
                                
                ===========================================================
                >>> TICKETLY DEV MODE SUMMARY <<<
                Active Profile(s)  : {}
                Total Events       : {}
                Total Reservations : {}
                ===========================================================
                """, activeProfiles, eventCount, reservationCount);
    }
}
