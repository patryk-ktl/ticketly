package io.github.patrykktl.ticketly.ticketingservice.model.command;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class CreateReservationCommand {

    private Integer eventId;
    private String customerEmail;
    private Integer seats;
}
