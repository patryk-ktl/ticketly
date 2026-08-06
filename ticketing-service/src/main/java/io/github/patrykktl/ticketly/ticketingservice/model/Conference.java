package io.github.patrykktl.ticketly.ticketingservice.model;

import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class Conference extends Event {

    private String topic;
    private Integer speakerCount;
    private Boolean hasWorkshops;
}
