package io.github.patrykktl.ticketly.ticketingservice.repository;

import io.github.patrykktl.ticketly.ticketingservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
}
