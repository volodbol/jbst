package io.tech1.framework.b2b.mongodb.security.jwt.handlers.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.dto.requests.RequestUserLogin;
import io.tech1.framework.b2b.mongodb.security.jwt.utilities.HttpRequestUtility;
import io.tech1.framework.domain.base.Password;
import io.tech1.framework.domain.base.Username;
import io.tech1.framework.domain.exceptions.ExceptionEntity;
import io.tech1.framework.incidents.domain.authetication.IncidentAuthenticationLoginFailureUsernameMaskedPassword;
import io.tech1.framework.incidents.domain.authetication.IncidentAuthenticationLoginFailureUsernamePassword;
import io.tech1.framework.incidents.events.publishers.IncidentPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtAuthenticationEntryPointExceptionHandler implements AuthenticationEntryPoint {

    // Incidents
    private final IncidentPublisher incidentPublisher;
    // Utilities
    private final HttpRequestUtility httpRequestUtility;
    // JSONs
    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(this.objectMapper.writeValueAsString(ExceptionEntity.of(exception)));

        // WARNING: in case of another endpoint to cache - extract methods like: isLoginEndpoint or isLogoutEndpoint
        if (exception instanceof BadCredentialsException && this.httpRequestUtility.isCachedEndpoint(request)) {
            var cachedPayload = this.httpRequestUtility.getCachedPayload(request);
            var requestUserLogin = this.objectMapper.readValue(cachedPayload, RequestUserLogin.class);

            var username = Username.of(requestUserLogin.getUsername());
            var password = Password.of(requestUserLogin.getPassword());
            this.incidentPublisher.publishAuthenticationLoginFailureUsernamePassword(
                    IncidentAuthenticationLoginFailureUsernamePassword.of(
                            username,
                            password
                    )
            );
            this.incidentPublisher.publishAuthenticationLoginFailureUsernameMaskedPassword(
                    IncidentAuthenticationLoginFailureUsernameMaskedPassword.of(
                            username,
                            password
                    )
            );
        }
    }
}
