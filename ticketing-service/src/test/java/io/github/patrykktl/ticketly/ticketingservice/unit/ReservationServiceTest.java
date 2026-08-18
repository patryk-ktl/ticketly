package io.github.patrykktl.ticketly.ticketingservice.unit;

import io.github.patrykktl.ticketly.ticketingservice.client.PaymentClient;
import io.github.patrykktl.ticketly.ticketingservice.exception.InterruptedPaymentException;
import io.github.patrykktl.ticketly.ticketingservice.exception.InvalidStatusException;
import io.github.patrykktl.ticketly.ticketingservice.exception.NoAvailableSeatsException;
import io.github.patrykktl.ticketly.ticketingservice.exception.SeatLimitReachedException;
import io.github.patrykktl.ticketly.ticketingservice.model.Concert;
import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import io.github.patrykktl.ticketly.ticketingservice.model.EventStatus;
import io.github.patrykktl.ticketly.ticketingservice.model.PaymentRequest;
import io.github.patrykktl.ticketly.ticketingservice.model.PaymentResponse;
import io.github.patrykktl.ticketly.ticketingservice.model.PaymentStatus;
import io.github.patrykktl.ticketly.ticketingservice.model.Reservation;
import io.github.patrykktl.ticketly.ticketingservice.model.ReservationStatus;
import io.github.patrykktl.ticketly.ticketingservice.model.command.CreateReservationCommand;
import io.github.patrykktl.ticketly.ticketingservice.model.dto.ReservationDto;
import io.github.patrykktl.ticketly.ticketingservice.properties.TicketlyProperties;
import io.github.patrykktl.ticketly.ticketingservice.repository.EventRepository;
import io.github.patrykktl.ticketly.ticketingservice.repository.ReservationRepository;
import io.github.patrykktl.ticketly.ticketingservice.service.ReservationService;
import io.github.patrykktl.ticketly.ticketingservice.service.ReservationTxHelper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketlyProperties properties;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private ReservationTxHelper txHelper;

    private Event defaultEvent;

    private CreateReservationCommand defaultCommand;

    private Reservation defaultReservation;

    @BeforeEach
    void setUp() {
        defaultEvent = Concert.builder()
                .id(7)
                .title("TestConcert")
                .venue("National Stadium")
                .city("Warsaw")
                .startsAt(LocalDateTime.now().plusDays(10))
                .basePrice(BigDecimal.valueOf(100))
                .totalSeats(10000)
                .availableSeats(1000)
                .status(EventStatus.ON_SALE)
                .createdAt(LocalDateTime.now())
                .version(0L)
                .artist("Tester")
                .genre("Test Music")
                .supportAct("Mockito")
                .build();

        defaultCommand = CreateReservationCommand.builder()
                .eventId(7)
                .customerEmail("test@test.com")
                .seats(10)
                .build();

        defaultReservation = Reservation.builder()
                .id(10)
                .status(ReservationStatus.PENDING_PAYMENT)
                .holdExpiresAt(LocalDateTime.now().plusMinutes(10))
                .customerEmail("test@test.com")
                .event(defaultEvent)
                .build();
    }

    @Test
    void testCreateReservation_shouldCreateReservation() {
        when(eventRepository.findWithLockingById(defaultCommand.getEventId())).thenReturn(Optional.of(defaultEvent));
        when(properties.getMaxSeatsPerReservation()).thenReturn(10);
        when(properties.getServiceFeePercent()).thenReturn(BigDecimal.valueOf(0.1));
        when(properties.getSeatHoldMinutes()).thenReturn(15);
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocationOnMock -> {
                    Reservation r = invocationOnMock.getArgument(0);
                    r.setId(100);
                    return r;
                });

        ReservationDto result = reservationService.createReservation(defaultCommand);

        assertThat(result).isNotNull();
        assertThat(result.getSeats()).isEqualTo(10);
        assertThat(result.getCustomerEmail()).isEqualTo("test@test.com");
        assertThat(result.getEventId()).isEqualTo(7);

        BigDecimal expectedPrice = BigDecimal.valueOf(1100);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(captor.capture());
        Reservation savedReservation = captor.getValue();

        assertThat(savedReservation.getSeats()).isEqualTo(10);
        assertThat(savedReservation.getCustomerEmail()).isEqualTo("test@test.com");
        assertThat(savedReservation.getStatus()).isEqualTo(ReservationStatus.PENDING_PAYMENT);
        assertThat(savedReservation.getTotalPrice()).isEqualByComparingTo(expectedPrice);
        assertThat(savedReservation.getHoldExpiresAt()).isAfter(savedReservation.getCreatedAt());
    }

    @Test
    void testCreateReservation_shouldThrowNoAvailableSeatsException() {
        defaultEvent.setAvailableSeats(1);

        when(eventRepository.findWithLockingById(defaultCommand.getEventId())).thenReturn(Optional.of(defaultEvent));

        assertThatThrownBy(() -> reservationService.createReservation(defaultCommand))
                .isInstanceOf(NoAvailableSeatsException.class)
                .hasMessage("There are not enough seats available for the selected event");

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCreateReservation_shouldThrowSeatLimitReachedException() {
        defaultCommand.setSeats(11);

        when(eventRepository.findWithLockingById(defaultCommand.getEventId())).thenReturn(Optional.of(defaultEvent));
        when(properties.getMaxSeatsPerReservation()).thenReturn(10);

        assertThatThrownBy(() -> reservationService.createReservation(defaultCommand))
                .isInstanceOf(SeatLimitReachedException.class)
                .hasMessage("You have exceeded the seat limit per reservation");

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCreateReservation_shouldThrowInvalidStatusException() {
        defaultEvent.setStatus(EventStatus.SOLD_OUT);

        when(eventRepository.findWithLockingById(defaultCommand.getEventId())).thenReturn(Optional.of(defaultEvent));

        assertThatThrownBy(() -> reservationService.createReservation(defaultCommand))
                .isInstanceOf(InvalidStatusException.class)
                .hasMessage("Reservations for the selected event are not on sale");

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testCreateReservation_shouldThrowEntityNotFoundException() {
        when(eventRepository.findWithLockingById(defaultCommand.getEventId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.createReservation(defaultCommand))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Event of given id cannot be found");

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testConfirm_shouldOrchestrateSuccessfulConfirmation() {
        PaymentResponse paymentResponse = new PaymentResponse(
                100,
                PaymentStatus.SUCCEEDED,
                "8082"
        );
        ReservationDto expectedDto = ReservationDto.builder()
                .id(10)
                .status(ReservationStatus.CONFIRMED)
                .build();

        when(txHelper.getAndValidateForConfirmation(10)).thenReturn(defaultReservation);
        when(paymentClient.charge(any(PaymentRequest.class))).thenReturn(paymentResponse);
        when(txHelper.recordPaymentOutcome(10, paymentResponse)).thenReturn(expectedDto);

        ReservationDto result = reservationService.confirm(10);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);

        ArgumentCaptor<PaymentRequest> captor = ArgumentCaptor.forClass(PaymentRequest.class);
        verify(paymentClient).charge(captor.capture());
        assertThat(captor.getValue().reservationId()).isEqualTo(10);

        verify(txHelper).getAndValidateForConfirmation(10);
        verify(txHelper).recordPaymentOutcome(10, paymentResponse);
    }

    @Test
    void testConfirm_shouldPropagateExceptionWhenPaymentFails() {
        PaymentResponse failedResponse = new PaymentResponse(
                100,
                PaymentStatus.FAILED,
                "8082"
        );

        when(txHelper.getAndValidateForConfirmation(10)).thenReturn(defaultReservation);
        when(paymentClient.charge(any(PaymentRequest.class))).thenReturn(failedResponse);
        when(txHelper.recordPaymentOutcome(10, failedResponse))
                .thenThrow(new InterruptedPaymentException("Payment declined, your hold remains until 18:00"));

        assertThatThrownBy(() -> reservationService.confirm(10))
                .isInstanceOf(InterruptedPaymentException.class)
                .hasMessageContaining("Payment declined");

        verify(txHelper).getAndValidateForConfirmation(10);
        verify(paymentClient).charge(any(PaymentRequest.class));
        verify(txHelper).recordPaymentOutcome(10, failedResponse);
    }
}
