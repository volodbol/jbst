package jbst.foundation.domain.properties;

import jbst.foundation.domain.properties.configs.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

@Slf4j
@ConfigurationProperties(
        prefix = "jbst",
        ignoreUnknownFields = false
)
@Data
public class JbstProperties implements PriorityOrdered {
    private ServerConfigs serverConfigs;
    private MavenConfigs mavenConfigs;
    private UtilitiesConfigs utilitiesConfigs;
    private AsyncConfigs asyncConfigs;
    private EventsConfigs eventsConfigs;
    private MvcConfigs mvcConfigs;
    private EmailConfigs emailConfigs;
    private IncidentConfigs incidentConfigs;
    private HardwareMonitoringConfigs hardwareMonitoringConfigs;
    private HardwareServerConfigs hardwareServerConfigs;
    private SecurityJwtConfigs securityJwtConfigs;
    private SecurityJwtWebsocketsConfigs securityJwtWebsocketsConfigs;
    private MongodbSecurityJwtConfigs mongodbSecurityJwtConfigs;

    public static JbstProperties hardcoded() {
        var properties = new JbstProperties();
        properties.setMavenConfigs(MavenConfigs.hardcoded());
        properties.setServerConfigs(ServerConfigs.hardcoded());
        properties.setUtilitiesConfigs(UtilitiesConfigs.hardcoded());
        properties.setAsyncConfigs(AsyncConfigs.hardcoded());
        properties.setEventsConfigs(EventsConfigs.hardcoded());
        properties.setMvcConfigs(MvcConfigs.hardcoded());
        properties.setEmailConfigs(EmailConfigs.hardcoded());
        properties.setIncidentConfigs(IncidentConfigs.hardcoded());
        properties.setHardwareMonitoringConfigs(HardwareMonitoringConfigs.hardcoded());
        properties.setHardwareServerConfigs(HardwareServerConfigs.hardcoded());
        properties.setSecurityJwtConfigs(SecurityJwtConfigs.hardcoded());
        properties.setSecurityJwtWebsocketsConfigs(SecurityJwtWebsocketsConfigs.hardcoded());
        properties.setMongodbSecurityJwtConfigs(MongodbSecurityJwtConfigs.hardcoded());
        return properties;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}