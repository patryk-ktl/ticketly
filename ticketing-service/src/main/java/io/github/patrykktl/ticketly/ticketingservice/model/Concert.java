package io.github.patrykktl.ticketly.ticketingservice.model;

import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class Concert extends Event {

    private String artist;
    private String genre;
    private String supportAct;
}
