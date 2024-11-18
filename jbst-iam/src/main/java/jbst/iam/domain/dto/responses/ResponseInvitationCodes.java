package jbst.iam.domain.dto.responses;

import java.util.List;
import java.util.Set;

public record ResponseInvitationCodes(
        Set<String> authorities,
        List<ResponseInvitation> invitationCodes
) {
}
