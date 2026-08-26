package config;

import observability.TrackExecutionAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import properties.TicketlyObservabilityProperties;
import observability.RequestLoggingFilter;

@AutoConfiguration
@EnableConfigurationProperties(TicketlyObservabilityProperties.class)
@ConditionalOnProperty(prefix = "ticketly.observability", name = "enabled", havingValue = "true")
public class TicketlyObservabilityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RequestLoggingFilter requestLoggingFilter() {
        return new RequestLoggingFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public TrackExecutionAspect trackExecutionAspect(TicketlyObservabilityProperties properties) {
        return new TrackExecutionAspect(properties.getSlowCallThresholdMs());
    }
}
