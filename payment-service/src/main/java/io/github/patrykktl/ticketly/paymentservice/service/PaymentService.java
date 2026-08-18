package io.github.patrykktl.ticketly.paymentservice.service;

import io.github.patrykktl.ticketly.paymentservice.exception.InterruptedPaymentException;
import io.github.patrykktl.ticketly.paymentservice.mapper.PaymentMapper;
import io.github.patrykktl.ticketly.paymentservice.model.Payment;
import io.github.patrykktl.ticketly.paymentservice.model.PaymentRequest;
import io.github.patrykktl.ticketly.paymentservice.model.PaymentResponse;
import io.github.patrykktl.ticketly.paymentservice.model.PaymentStatus;
import io.github.patrykktl.ticketly.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final String serverPort;
    private final Random random;

    public PaymentService(PaymentRepository paymentRepository, @Value("${server.port}") String serverPort) {
        this.paymentRepository = paymentRepository;
        this.serverPort = serverPort;
        this.random = new Random();
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        simulateProcessingDelay();

        PaymentStatus status = (random.nextDouble() < 0.90)
                ? PaymentStatus.SUCCEEDED
                : PaymentStatus.FAILED;

        Payment payment = Payment.builder()
                .reservationId(request.reservationId())
                .amount(request.amount())
                .status(status)
                .processedAt(LocalDateTime.now())
                .processedBy(serverPort)
                .build();

        return PaymentMapper.mapToResponse(paymentRepository.save(payment));
    }

    private void simulateProcessingDelay() {
        int delayMs = 200 + random.nextInt(601);
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedPaymentException("Payment processing interrupted");
        }
    }
}
