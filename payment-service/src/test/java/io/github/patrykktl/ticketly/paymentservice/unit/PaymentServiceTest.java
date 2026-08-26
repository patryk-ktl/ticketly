package io.github.patrykktl.ticketly.paymentservice.unit;

import dto.PaymentRequest;
import dto.PaymentResponse;
import dto.PaymentStatus;
import io.github.patrykktl.ticketly.paymentservice.model.Payment;
import io.github.patrykktl.ticketly.paymentservice.repository.PaymentRepository;
import io.github.patrykktl.ticketly.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private Random random;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, "8082");
        ReflectionTestUtils.setField(paymentService, "random", random);
    }

    @Test
    void testProcessPayment_shouldReturnResponse() {
        Integer reservationId = 1;
        Integer paymentId = 1;
        PaymentRequest request = new PaymentRequest(reservationId, new BigDecimal("99.99"));

        when(random.nextInt(601)).thenReturn(0); // Bypasses sleep delay
        when(random.nextDouble()).thenReturn(0.50); // < 0.90 triggers SUCCEEDED

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment savedPayment = invocation.getArgument(0);
            savedPayment.setId(paymentId);
            return savedPayment;
        });

        PaymentResponse response = paymentService.processPayment(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(paymentId);
        assertThat(response.status()).isEqualTo(PaymentStatus.SUCCEEDED);
        assertThat(response.processedBy()).isEqualTo("8082");

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}
