package io.github.patrykktl.ticketly.ticketingservice.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Conference extends Event {

    private String topic;
    private Integer speakerCount;
    private Boolean hasWorkshops;
}
