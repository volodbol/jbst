package io.tech1.framework.domain.hardware.monitoring;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

import static io.tech1.framework.domain.asserts.Asserts.assertNonNullOrThrow;
import static io.tech1.framework.domain.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class HardwareMonitoringThresholds {
    private final Map<HardwareName, HardwareMonitoringThreshold> thresholds;

    public HardwareMonitoringThresholds(
            Map<HardwareName, BigDecimal> thresholds
    ) {
        assertNonNullOrThrow(thresholds, invalidAttribute("HardwareMonitoringThresholds.thresholds"));
        this.thresholds = thresholds.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new HardwareMonitoringThreshold(entry.getValue())
                )
        );
    }
}
