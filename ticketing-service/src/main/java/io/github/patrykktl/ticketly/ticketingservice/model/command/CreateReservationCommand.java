package io.github.patrykktl.ticketly.ticketingservice.model.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CreateReservationCommand {

    @Positive(message = "NEGATIVE_OR_ZERO_VALUE")
    @NotNull(message = "NULL_VALUE")
    private Integer eventId;

    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "BLANK_VALUE")
    private String customerEmail;

    @Min(1)
    @NotNull(message = "NULL_VALUE")
    private Integer seats;
}
