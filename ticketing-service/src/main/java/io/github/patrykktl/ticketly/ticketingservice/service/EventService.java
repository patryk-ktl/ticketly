package io.github.patrykktl.ticketly.ticketingservice.service;

import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import io.github.patrykktl.ticketly.ticketingservice.model.command.EventSearchCriteria;
import io.github.patrykktl.ticketly.ticketingservice.model.dto.EventCardDto;
import io.github.patrykktl.ticketly.ticketingservice.repository.EventRepository;
import io.github.patrykktl.ticketly.ticketingservice.repository.specification.EventSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Page<EventCardDto> searchEvents(EventSearchCriteria criteria, Pageable pageable) {
        Specification<Event> spec = EventSpecifications.buildSpecification(criteria);
        return eventRepository.findBy(
                spec,
                q -> q.as(EventCardDto.class).page(pageable));
    }
}
