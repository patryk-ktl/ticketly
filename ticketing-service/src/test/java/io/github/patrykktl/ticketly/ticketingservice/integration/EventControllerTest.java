package io.github.patrykktl.ticketly.ticketingservice.integration;

import io.github.patrykktl.ticketly.ticketingservice.model.Concert;
import io.github.patrykktl.ticketly.ticketingservice.model.Conference;
import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import io.github.patrykktl.ticketly.ticketingservice.model.EventStatus;
import io.github.patrykktl.ticketly.ticketingservice.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();

        Event event1 = Concert.builder()
                .title("Rock Concert")
                .city("Warsaw")
                .venue("National Stadium")
                .startsAt(LocalDateTime.now().plusDays(5))
                .basePrice(new BigDecimal("150.00"))
                .totalSeats(10000)
                .availableSeats(100)
                .status(EventStatus.ON_SALE)
                .createdAt(LocalDateTime.now())
                .artist("The Tester")
                .genre("Test Music")
                .supportAct("Mockito")
                .build();

        Event event2 = Conference.builder()
                .title("Tech Conference")
                .city("Warsaw")
                .venue("National Stadium")
                .startsAt(LocalDateTime.now().plusDays(10))
                .basePrice(new BigDecimal("300.00"))
                .totalSeats(10000)
                .availableSeats(50)
                .status(EventStatus.ON_SALE)
                .createdAt(LocalDateTime.now())
                .topic("testing")
                .speakerCount(100)
                .hasWorkshops(true)
                .build();

        Event event3 = Concert.builder()
                .title("Jazz Night")
                .city("Krakow")
                .venue("City Hall")
                .startsAt(LocalDateTime.now().plusDays(2))
                .basePrice(new BigDecimal("80.00"))
                .totalSeats(10000)
                .availableSeats(30)
                .status(EventStatus.ON_SALE)
                .createdAt(LocalDateTime.now())
                .artist("Jazz Tester")
                .genre("Testt Music")
                .supportAct("Mockito")
                .build();

        eventRepository.saveAll(List.of(event1, event2, event3));
    }

    @Test
    void testSearchEvents_shouldReturnFilteredPage() throws Exception {
        mockMvc.perform(get("/api/events")
                        .param("city", "Warsaw")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].city").value("Warsaw"))
                .andExpect(jsonPath("$.content[1].city").value("Warsaw"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0));
    }
}
