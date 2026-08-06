package io.github.patrykktl.ticketly.ticketingservice.repository;

import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
}
