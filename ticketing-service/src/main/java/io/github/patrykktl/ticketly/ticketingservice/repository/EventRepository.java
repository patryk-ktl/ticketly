package io.github.patrykktl.ticketly.ticketingservice.repository;

import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Event> findWithLockingById(Integer id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                UPDATE Event e
                SET e.availableSeats = e.availableSeats + COALESCE((
                    SELECT SUM(r.seats)
                    FROM Reservation r
                    WHERE r.event = e
                      AND r.status = 'PENDING_PAYMENT'
                      AND r.holdExpiresAt < :now
                ), 0)
                WHERE EXISTS (
                    SELECT 1
                    FROM Reservation r
                    WHERE r.event = e
                      AND r.status = 'PENDING_PAYMENT'
                      AND r.holdExpiresAt < :now
                )
            """)
    int restoreSeatsWithExpiredReservations(@Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                UPDATE Event e
                SET e.status = 'FINISHED'
                WHERE e.startsAt < :now
                 AND e.status = 'ON_SALE'
            """)
    int finishPastEvents(@Param("now") LocalDateTime now);
}
