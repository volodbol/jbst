package io.tech1.framework.domain.properties.configs.security.jwt;

import io.tech1.framework.domain.properties.annotations.MandatoryProperty;
import io.tech1.framework.domain.properties.base.AbstractPropertyConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class LoggingConfigs extends AbstractPropertyConfigs {
    @MandatoryProperty
    private final Boolean advancedRequestLoggingEnabled;

    public static LoggingConfigs testsHardcoded() {
        return LoggingConfigs.enabled();
    }

    public static LoggingConfigs enabled() {
        return new LoggingConfigs(true);
    }

    public static LoggingConfigs disabled() {
        return new LoggingConfigs(false);
    }

    public boolean isAdvancedRequestLoggingEnabled() {
        return this.advancedRequestLoggingEnabled;
    }
}
