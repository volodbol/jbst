package jbst.foundation.domain.properties.configs;

import jbst.foundation.domain.hardware.monitoring.HardwareName;
import jbst.foundation.domain.properties.annotations.MandatoryMapProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class HardwareMonitoringConfigs extends AbstractTogglePropertiesConfigs {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryToggleProperty
    @MandatoryMapProperty(propertyName = "thresholdsConfigs", keySetClass = HardwareName.class)
    private Map<HardwareName, BigDecimal> thresholdsConfigs;

    public static HardwareMonitoringConfigs hardcoded() {
        return new HardwareMonitoringConfigs(
                true,
                new EnumMap<>(
                        Map.of(
                                HardwareName.CPU, new BigDecimal("80"),
                                HardwareName.HEAP, new BigDecimal("85"),
                                HardwareName.SERVER, new BigDecimal("90"),
                                HardwareName.SWAP, new BigDecimal("95"),
                                HardwareName.VIRTUAL, new BigDecimal("98")
                        )
                )
        );
    }

    public static HardwareMonitoringConfigs disabled() {
        return new HardwareMonitoringConfigs(false, new EnumMap<>(HardwareName.class));
    }

    public Map<HardwareName, BigDecimal> getThresholdsConfigs() {
        if (this.enabled) {
            return this.thresholdsConfigs;
        } else {
            return Stream.of(HardwareName.values())
                    .collect(
                            Collectors.toMap(
                                    entry -> entry,
                                    entry -> BigDecimal.ZERO
                            )
                    );
        }
    }

    @Override
    public boolean isParentPropertiesNode() {
        return true;
    }
}
