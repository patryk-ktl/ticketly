package io.github.patrykktl.ticketly.paymentservice.repository;

import io.github.patrykktl.ticketly.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
