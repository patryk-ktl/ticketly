package io.github.patrykktl.ticketly.ticketingservice.unit;

import io.github.patrykktl.ticketly.ticketingservice.model.dto.EventCardDto;
import io.github.patrykktl.ticketly.ticketingservice.repository.EventRepository;
import io.github.patrykktl.ticketly.ticketingservice.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    private EventCardDto dto;

    @BeforeEach
    void setUp() {
        dto = EventCardDto.builder()
                .id(7)
                .title("Test")
                .city("Warsaw")
                .startsAt(LocalDateTime.now().plusDays(10))
                .basePrice(BigDecimal.valueOf(100.00))
                .availableSeats(100)
                .build();
    }

    @Test
    void testSearchEvents_shouldReturnPage() {
        Page<EventCardDto> eventDtoPage = new PageImpl<>(List.of(dto));
        Pageable pageable = Pageable.unpaged();

        when(eventRepository.findBy(any(Specification.class), any(Function.class))).thenReturn(eventDtoPage);

        Page<EventCardDto> result = eventService.searchEvents(null, pageable).toPage(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(dto);
        verify(eventRepository).findBy(any(Specification.class), any(Function.class));
    }
}
