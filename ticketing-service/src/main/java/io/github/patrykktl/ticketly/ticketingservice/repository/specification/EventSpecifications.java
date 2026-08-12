package io.github.patrykktl.ticketly.ticketingservice.repository.specification;

import io.github.patrykktl.ticketly.ticketingservice.model.Concert;
import io.github.patrykktl.ticketly.ticketingservice.model.Conference;
import io.github.patrykktl.ticketly.ticketingservice.model.Event;
import io.github.patrykktl.ticketly.ticketingservice.model.EventStatus;
import io.github.patrykktl.ticketly.ticketingservice.model.SportsMatch;
import io.github.patrykktl.ticketly.ticketingservice.model.command.EventSearchCriteria;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@UtilityClass
public class EventSpecifications {

    public static Specification<Event> inCity(String city) {
        return (root, query, cb) ->
                (city == null || city.isBlank())
                        ? cb.conjunction()
                        : cb.equal(cb.lower(root.get("city")), city.toLowerCase().trim());
    }

    public static Specification<Event> startsBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();

            LocalDateTime startDateTime = (from != null) ? from.atStartOfDay() : null;
            LocalDateTime endDateTime = (to != null) ? to.atTime(LocalTime.MAX) : null;

            if (startDateTime != null && endDateTime != null) {
                return cb.between(root.get("startsAt"), startDateTime, endDateTime);
            } else if (startDateTime != null) {
                return cb.greaterThanOrEqualTo(root.get("startsAt"), startDateTime);
            } else {
                return cb.lessThanOrEqualTo(root.get("startsAt"), endDateTime);
            }
        };
    }

    public static Specification<Event> priceAtMost(BigDecimal maxPrice) {
        return (root, query, cb) ->
                (maxPrice == null)
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice);
    }

    public static Specification<Event> hasAvailableSeats() {
        return (root, query, cb) -> cb.and(
                cb.greaterThan(root.get("availableSeats"), 0),
                cb.equal(root.get("status"), EventStatus.ON_SALE)
        );
    }

    public static Specification<Event> titleContains(String chars) {
        return (root, query, cb) ->
                (chars == null || chars.isBlank())
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("title")), "%" + chars.toLowerCase().trim());
    }

    public static Specification<Event> ofType(String type) {
        Class<? extends Event> clazz = resolveEventClass(type);

        return (root, query, cb) ->
                (clazz == null)
                        ? cb.conjunction()
                        : cb.equal(root.type(), clazz);
    }

    private static Class<? extends Event> resolveEventClass(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }

        return switch (type.trim().toUpperCase()) {
            case "CONCERT" -> Concert.class;
            case "CONFERENCE" -> Conference.class;
            case "SPORTS_MATCH", "SPORTSMATCH" -> SportsMatch.class;
            default -> null;
        };
    }

    public static Specification<Event> buildSpecification(EventSearchCriteria criteria) {
        if (criteria == null) return Specification.where(hasAvailableSeats());

        return Specification.allOf(
                inCity(criteria.city()),
                startsBetween(criteria.from(), criteria.to()),
                priceAtMost(criteria.maxPrice()),
                hasAvailableSeats(),
                titleContains(criteria.title()),
                ofType(criteria.type())
        );
    }
}
