package io.github.patrykktl.ticketly.ticketingservice.model.command;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EventSearchCriteria(
        String city,
        BigDecimal maxPrice,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        Boolean availableOnly,
        String title,
        String type
) {
}
