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
public class SportsMatch extends Event {

    private String homeTeam;
    private String awayTeam;
    private String league;
}
