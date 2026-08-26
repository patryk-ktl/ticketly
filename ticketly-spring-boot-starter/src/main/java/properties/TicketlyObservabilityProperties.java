package properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("ticketly.observability")
public class TicketlyObservabilityProperties {

    private boolean enabled = false;

    private long slowCallThresholdMs = 500;
}
