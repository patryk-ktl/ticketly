package io.github.patrykktl.ticketly.ticketingservice.client;

import io.github.patrykktl.ticketly.ticketingservice.model.PaymentRequest;
import io.github.patrykktl.ticketly.ticketingservice.model.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", fallback = PaymentClientFallback.class)
public interface PaymentClient {

    @PostMapping("/api/payments")
    PaymentResponse charge(@RequestBody PaymentRequest request);
}
