package io.github.patrykktl.ticketly.ticketingservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public abstract class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String venue;
    private String city;
    private LocalDateTime startsAt;
    private BigDecimal basePrice;
    private Integer totalSeats;
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "reservation")
    private final List<Reservation> reservations = new ArrayList<>();
}
