package io.github.patrykktl.ticketly.ticketingservice.config;

import org.slf4j.LoggerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerLoggingConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerLoggingCustomizer() {
        return factory -> factory.addCircuitBreakerCustomizer(
                circuitBreaker -> circuitBreaker.getEventPublisher().onStateTransition(event ->
                        LoggerFactory.getLogger(CircuitBreakerLoggingConfig.class)
                                .warn("Payment circuit breaker state transition: {} -> {}",
                                        event.getStateTransition().getFromState(),
                                        event.getStateTransition().getToState())),
                "payment-service"
        );
    }
}
