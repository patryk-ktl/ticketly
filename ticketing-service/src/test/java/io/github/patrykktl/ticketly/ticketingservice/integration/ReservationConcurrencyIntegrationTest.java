package io.github.patrykktl.ticketly.ticketingservice.integration;

import io.github.patrykktl.ticketly.ticketingservice.model.Concert;
import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import io.github.patrykktl.ticketly.ticketingservice.model.EventStatus;
import io.github.patrykktl.ticketly.ticketingservice.model.command.CreateReservationCommand;
import io.github.patrykktl.ticketly.ticketingservice.repository.EventRepository;
import io.github.patrykktl.ticketly.ticketingservice.repository.ReservationRepository;
import io.github.patrykktl.ticketly.ticketingservice.service.ReservationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationConcurrencyIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private Integer testEventId;

    @Test
    @DisplayName("Should fail overbooking under high concurrency when locks are absent")
    void reproduceOverbookingRaceCondition() throws InterruptedException {

        Concert concert = Concert.builder()
                .title("Concurrent Race Condition Concert")
                .venue("Stadium")
                .city("Gotham")
                .startsAt(LocalDateTime.now().plusDays(7))
                .basePrice(BigDecimal.valueOf(100))
                .totalSeats(100)
                .availableSeats(1)
                .status(EventStatus.ON_SALE)
                .createdAt(LocalDateTime.now())
                .artist("Race Condition")
                .genre("PROBLEM")
                .supportAct("The Lock")
                .build();
        Event savedEvent = eventRepository.save(concert);
        this.testEventId = savedEvent.getId();

        try {
            int numberOfThreads = 2;
            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);

            Runnable task = () -> {
                try {
                    startLatch.await();
                    reservationService.createReservation(
                            new CreateReservationCommand(testEventId, "ex@example.com", 1)
                    );
                } catch (Exception ignored) {
                    //expected failure for loser thread once locked
                } finally {
                    finishLatch.countDown();
                }
            };

            executor.submit(task);
            executor.submit(task);

            startLatch.countDown();
            finishLatch.await(5, TimeUnit.SECONDS);
            executor.shutdown();

            Event updatedEvent = eventRepository.findById(testEventId).orElseThrow();
            long totalReservations = reservationRepository.countByEventId(testEventId);

            assertThat(totalReservations).isEqualTo(1);
            assertThat(updatedEvent.getAvailableSeats()).isZero();

        } catch (Exception ignored) {
            //ignored
        }
    }

    @AfterEach
    void tearDown() {
        transactionTemplate.executeWithoutResult(status -> {
            reservationRepository.deleteByEventId(testEventId);
            eventRepository.deleteById(testEventId);
        });
    }
}
