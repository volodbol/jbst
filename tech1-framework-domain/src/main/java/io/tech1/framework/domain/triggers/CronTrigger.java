package io.tech1.framework.domain.triggers;

import com.fasterxml.jackson.annotation.JsonValue;
import io.tech1.framework.domain.base.Username;

public record CronTrigger() implements AbstractTrigger {

    @Override
    public Username getUsername() {
        return Username.cron();
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.CRON;
    }

    @JsonValue
    @Override
    public String getReadableDetails() {
        return this.getTriggerType().getValue() + " trigger";
    }
}
