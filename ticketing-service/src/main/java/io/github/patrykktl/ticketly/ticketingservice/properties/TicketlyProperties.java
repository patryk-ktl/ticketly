package io.github.patrykktl.ticketly.ticketingservice.properties;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
@ConfigurationProperties(prefix = "ticketly")
@Getter
@Setter
public class TicketlyProperties {

    @Min(value = 1, message = "Seat hold duration must be at least 1 minute")
    Integer seatHoldMinutes;

    @Min(value = 1, message = "Max seats per reservation must be at least 1")
    Integer maxSeatsPerReservation;

    @DecimalMin(value = "0.0", message = "Service fee percentage cannot be negative")
    BigDecimal serviceFeePercent;

    @NotBlank(message = "Hold cleanup cron expression must not be blank")
    String holdCleanupCron;

    @NotBlank(message = "Finish events cron expression must not be blank")
    String finishEventsCron;
}
