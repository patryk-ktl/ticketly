package io.github.patrykktl.ticketly.ticketingservice.controller;

import io.github.patrykktl.ticketly.ticketingservice.model.command.EventSearchCriteria;
import io.github.patrykktl.ticketly.ticketingservice.model.dto.EventCardDto;
import io.github.patrykktl.ticketly.ticketingservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public Page<EventCardDto> searchEvents(EventSearchCriteria criteria, Pageable pageable) {
        return eventService.searchEvents(criteria, pageable).toPage(pageable);
    }

    @GetMapping("/{id}")
    public EventCardDto getEventDetails(@PathVariable Integer id) {
        return eventService.getEventDetails(id);
    }
}
