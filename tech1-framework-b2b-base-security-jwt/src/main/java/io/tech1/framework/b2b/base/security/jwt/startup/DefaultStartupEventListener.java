package io.tech1.framework.b2b.base.security.jwt.startup;

import io.tech1.framework.b2b.base.security.jwt.essense.AbstractEssenceConstructor;
import io.tech1.framework.domain.enums.Status;
import io.tech1.framework.domain.utilities.printer.PRINTER;
import io.tech1.framework.properties.ApplicationFrameworkProperties;
import io.tech1.framework.utilities.environment.EnvironmentUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.tech1.framework.domain.constants.FrameworkLogsConstants.FRAMEWORK_B2B_SECURITY_JWT_PREFIX;
import static io.tech1.framework.domain.constants.FrameworkLogsConstants.LINE_SEPARATOR_INTERPUNCT;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DefaultStartupEventListener implements BaseStartupEventListener {
    private static final String STARTUP_MESSAGE = FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " Default startup event listener. Status: `{}`";

    // Essence
    protected final AbstractEssenceConstructor essenceConstructor;
    // Utilities
    protected final EnvironmentUtility environmentUtility;
    // Properties
    protected final ApplicationFrameworkProperties applicationFrameworkProperties;

    @Override
    public void onStartup() {
        PRINTER.info(LINE_SEPARATOR_INTERPUNCT);
        PRINTER.info(STARTUP_MESSAGE, Status.STARTED);

        this.environmentUtility.verifyProfilesConfiguration();

        if (this.applicationFrameworkProperties.getSecurityJwtConfigs().getEssenceConfigs().getDefaultUsers().isEnabled()) {
            PRINTER.info(FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " Essence `defaultUsers` is enabled");
            this.essenceConstructor.addDefaultUsers();
        } else {
            PRINTER.info(FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " Essence `defaultUsers` is disabled");
        }
        if (this.applicationFrameworkProperties.getSecurityJwtConfigs().getEssenceConfigs().getInvitationCodes().isEnabled()) {
            PRINTER.info(FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " Essence `invitationCodes` is enabled");
            this.essenceConstructor.addDefaultUsersInvitationCodes();
        } else {
            PRINTER.info(FRAMEWORK_B2B_SECURITY_JWT_PREFIX + " Essence `invitationCodes` is disabled");
        }
        PRINTER.info(STARTUP_MESSAGE, Status.COMPLETED);
        PRINTER.info(LINE_SEPARATOR_INTERPUNCT);
    }
}
