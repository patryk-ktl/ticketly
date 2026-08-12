package io.github.patrykktl.ticketly.ticketingservice.repository;

import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import io.github.patrykktl.ticketly.ticketingservice.model.dto.EventCardDto;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Event> findWithLockingById(Integer id);

    @Query("select e from Event e")
    Page<EventCardDto> findAllDto(Pageable pageable);
}
