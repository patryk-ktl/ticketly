package io.github.patrykktl.ticketly.ticketingservice.integration;

import dto.PaymentRequest;
import dto.PaymentResponse;
import dto.PaymentStatus;
import io.github.patrykktl.ticketly.ticketingservice.client.PaymentClient;
import io.github.patrykktl.ticketly.ticketingservice.model.Conference;
import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import io.github.patrykktl.ticketly.ticketingservice.model.EventStatus;
import io.github.patrykktl.ticketly.ticketingservice.model.ReservationStatus;
import io.github.patrykktl.ticketly.ticketingservice.model.command.CreateReservationCommand;
import io.github.patrykktl.ticketly.ticketingservice.repository.EventRepository;
import io.github.patrykktl.ticketly.ticketingservice.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockitoBean
    private PaymentClient paymentClient;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        eventRepository.deleteAll();

        testEvent = Conference.builder()
                .title("System Architecture Summit")
                .city("Warsaw")
                .venue("National Stadium")
                .startsAt(LocalDateTime.now().plusDays(15))
                .basePrice(new BigDecimal("100.00"))
                .totalSeats(10000)
                .availableSeats(10)
                .status(EventStatus.ON_SALE)
                .createdAt(LocalDateTime.now())
                .topic("sys architecture")
                .speakerCount(100)
                .hasWorkshops(true)
                .build();

        testEvent = eventRepository.save(testEvent);
    }

    @Test
    void fullReservationLifecycle_ShouldSucceedAndDecreaseSeats() throws Exception {
        PaymentResponse mockPaymentResponse = new PaymentResponse(
                100,
                PaymentStatus.SUCCEEDED,
                "8082"
        );
        when(paymentClient.charge(any(PaymentRequest.class))).thenReturn(mockPaymentResponse);

        CreateReservationCommand command = CreateReservationCommand.builder()
                .eventId(testEvent.getId())
                .customerEmail("developer@example.com")
                .seats(3)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerEmail").value("developer@example.com"))
                .andExpect(jsonPath("$.seats").value(3))
                .andExpect(jsonPath("$.status").value(ReservationStatus.PENDING_PAYMENT.name()))
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        Integer reservationId = objectMapper.readTree(responseJson).get("id").asInt();

        mockMvc.perform(post("/api/reservations/{id}/confirm", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.status").value(ReservationStatus.CONFIRMED.name()));

        mockMvc.perform(get("/api/reservations/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ReservationStatus.CONFIRMED.name()));

        Event updatedEvent = eventRepository.findById(testEvent.getId()).orElseThrow();
        assertThat(updatedEvent.getAvailableSeats()).isEqualTo(7);
    }

    @Test
    void testCreateReservation_Overbooking_ShouldReturn409Conflict() throws Exception {
        CreateReservationCommand command = CreateReservationCommand.builder()
                .eventId(testEvent.getId())
                .customerEmail("overbook@example.com")
                .seats(15)
                .build();

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("There are not enough seats available for the selected event"));
    }

    @Test
    void testCreateReservation_InvalidPayload_ShouldReturn400BadRequest() throws Exception {
        CreateReservationCommand invalidCommand = CreateReservationCommand.builder()
                .eventId(testEvent.getId())
                .customerEmail("not-an-email")
                .seats(0)
                .build();

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommand)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations[*].field", hasItem("customerEmail")))
                .andExpect(jsonPath("$.violations[*].field", hasItem("seats")));
    }
}
