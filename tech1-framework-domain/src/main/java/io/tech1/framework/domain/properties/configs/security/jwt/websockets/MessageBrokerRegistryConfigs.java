package io.tech1.framework.domain.properties.configs.security.jwt.websockets;

import io.tech1.framework.domain.properties.annotations.MandatoryProperty;
import io.tech1.framework.domain.properties.configs.AbstractPropertiesConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageBrokerRegistryConfigs extends AbstractPropertiesConfigs {
    // INFO: spring support list of prefixes as varargs
    @MandatoryProperty
    private final String applicationDestinationPrefix;
    // INFO: spring support list of destinations as varargs
    @MandatoryProperty
    private final String simpleDestination;
    @MandatoryProperty
    private final String userDestinationPrefix;

    public static MessageBrokerRegistryConfigs testsHardcoded() {
        return new MessageBrokerRegistryConfigs("/app", "/queue", "/user");
    }
}