package tech1.framework.foundation.domain.triggers;

import com.fasterxml.jackson.annotation.JsonValue;
import tech1.framework.foundation.domain.base.Username;

public record UserTrigger(Username username) implements AbstractTrigger {

    @Override
    public Username getUsername() {
        return this.username;
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.USER;
    }

    @JsonValue
    @Override
    public String getReadableDetails() {
        return this.getTriggerType().getValue() + " trigger, username: " + this.username;
    }
}