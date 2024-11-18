package jbst.iam.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.constants.ZoneIdsConstants;

import java.time.ZoneId;

import static jbst.foundation.utilities.zones.ZonesUtility.reworkUkraineZoneId;

public record RequestUserUpdate1(
        @Schema(type = "string") @NotNull ZoneId zoneId,
        @Email.ValidEmail Email email,
        String name
) {

    public static RequestUserUpdate1 testsHardcoded() {
        return new RequestUserUpdate1(
                ZoneIdsConstants.UKRAINE,
                Email.hardcoded(),
                "Tech1 Ops"
        );
    }

    public RequestUserUpdate1 createReworkedUkraineZoneId() {
        return new RequestUserUpdate1(
                reworkUkraineZoneId(this.zoneId),
                this.email,
                this.name
        );
    }
}
