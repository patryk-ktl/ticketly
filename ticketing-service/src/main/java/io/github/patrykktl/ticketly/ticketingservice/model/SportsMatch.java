package io.github.patrykktl.ticketly.ticketingservice.model;

import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class SportsMatch extends Event {

    private String homeTeam;
    private String awayTeam;
    private String league;
}
