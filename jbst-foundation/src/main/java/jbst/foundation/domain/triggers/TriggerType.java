package jbst.foundation.domain.triggers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jbst.foundation.domain.enums.EnumValue;
import lombok.AllArgsConstructor;

import static jbst.foundation.utilities.enums.EnumCreatorUtility.findEnumByValueIgnoreCaseOrThrow;

@AllArgsConstructor
public enum TriggerType implements EnumValue<String> {
    AUTO("Auto"),
    CRON("Cron"),
    USER("User");

    private final String value;

    @JsonCreator
    public static TriggerType find(String value) {
        return findEnumByValueIgnoreCaseOrThrow(TriggerType.class, value);
    }

    @JsonValue
    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public boolean isAuto() {
        return AUTO.equals(this);
    }

    public boolean isCron() {
        return CRON.equals(this);
    }

    public boolean isUser() {
        return USER.equals(this);
    }
}
