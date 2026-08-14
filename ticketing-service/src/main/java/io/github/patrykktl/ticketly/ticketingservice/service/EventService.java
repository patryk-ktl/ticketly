package io.github.patrykktl.ticketly.ticketingservice.service;

import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import io.github.patrykktl.ticketly.ticketingservice.model.command.EventSearchCriteria;
import io.github.patrykktl.ticketly.ticketingservice.model.dto.EventCardDto;
import io.github.patrykktl.ticketly.ticketingservice.repository.EventRepository;
import io.github.patrykktl.ticketly.ticketingservice.repository.specification.EventSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Cacheable(value = "eventSearchResults", keyGenerator = "searchKeyGenerator")
    public Page<EventCardDto> searchEvents(EventSearchCriteria criteria, Pageable pageable) {
        Specification<Event> spec = EventSpecifications.buildSpecification(criteria);
        return eventRepository.findBy(
                spec,
                q -> q.as(EventCardDto.class).page(pageable));
    }

    @Cacheable(value = "eventDetails", key = "#id")
    public EventCardDto getEventDetails(Integer id) {
        return eventRepository.findEventDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Event of given id cannot be found"));
    }
}
