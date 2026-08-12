package io.github.patrykktl.ticketly.ticketingservice.repository;

import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Event> findWithLockingById(Integer id);
}
