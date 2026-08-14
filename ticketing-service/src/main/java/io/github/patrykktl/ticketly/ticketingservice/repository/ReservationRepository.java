package io.github.patrykktl.ticketly.ticketingservice.repository;

import io.github.patrykktl.ticketly.ticketingservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    void deleteByEventId(Integer eventId);

    long countByEventId(Integer eventId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                    UPDATE Reservation r
                    SET r.status = 'EXPIRED'
                    WHERE r.status = 'PENDING_PAYMENT'
                      AND r.holdExpiresAt < :now
            """)
    int expirePendingHolds(@Param("now") LocalDateTime now);
}
