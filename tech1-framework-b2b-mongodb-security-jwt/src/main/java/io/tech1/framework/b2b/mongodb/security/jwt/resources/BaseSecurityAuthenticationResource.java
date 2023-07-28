package io.tech1.framework.b2b.mongodb.security.jwt.resources;


import io.tech1.framework.b2b.base.security.jwt.annotations.AbstractFrameworkBaseSecurityResource;
import io.tech1.framework.b2b.base.security.jwt.domain.dto.requests.RequestUserLogin;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtRefreshToken;
import io.tech1.framework.b2b.base.security.jwt.domain.sessions.Session;
import io.tech1.framework.b2b.base.security.jwt.sessions.SessionRegistry;
import io.tech1.framework.b2b.base.security.jwt.validators.AuthenticationRequestsValidator;
import io.tech1.framework.b2b.mongodb.security.jwt.assistants.core.CurrentSessionAssistant;
import io.tech1.framework.b2b.mongodb.security.jwt.assistants.userdetails.JwtUserDetailsAssistant;
import io.tech1.framework.b2b.mongodb.security.jwt.cookies.CookieProvider;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.dto.responses.ResponseUserSession1;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.security.CurrentClientUser;
import io.tech1.framework.b2b.mongodb.security.jwt.services.TokenService;
import io.tech1.framework.b2b.mongodb.security.jwt.services.UserSessionService;
import io.tech1.framework.b2b.mongodb.security.jwt.utilities.SecurityJwtTokenUtility;
import io.tech1.framework.domain.exceptions.cookie.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static io.tech1.framework.domain.enums.Status.COMPLETED;
import static io.tech1.framework.domain.enums.Status.STARTED;
import static java.util.Objects.nonNull;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@AbstractFrameworkBaseSecurityResource
@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseSecurityAuthenticationResource {

    // Authentication
    private final AuthenticationManager authenticationManager;
    // Sessions
    private final SessionRegistry sessionRegistry;
    // Services
    private final UserSessionService userSessionService;
    private final TokenService tokenService;
    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    private final JwtUserDetailsAssistant jwtUserDetailsAssistant;
    // Cookies
    private final CookieProvider cookieProvider;
    // Validators
    private final AuthenticationRequestsValidator authenticationRequestsValidator;
    // Utilities
    private final SecurityJwtTokenUtility securityJwtTokenUtility;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public CurrentClientUser login(@RequestBody RequestUserLogin requestUserLogin, HttpServletRequest request, HttpServletResponse response) {
        this.authenticationRequestsValidator.validateLoginRequest(requestUserLogin);
        var username = requestUserLogin.username();
        var password = requestUserLogin.password();
        LOGGER.info("Login attempt. Username: `{}`. Status: `{}`", username, STARTED);

        var authenticationToken = new UsernamePasswordAuthenticationToken(username.identifier(), password.value());
        var authentication = this.authenticationManager.authenticate(authenticationToken);

        var jwtUser = this.jwtUserDetailsAssistant.loadUserByUsername(username.identifier());
        var dbUser = jwtUser.dbUser();

        var jwtAccessToken = this.securityJwtTokenUtility.createJwtAccessToken(dbUser);
        var jwtRefreshToken = this.securityJwtTokenUtility.createJwtRefreshToken(dbUser);

        this.userSessionService.save(dbUser, jwtRefreshToken, request);

        this.cookieProvider.createJwtAccessCookie(jwtAccessToken, response);
        this.cookieProvider.createJwtRefreshCookie(jwtRefreshToken, response);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        LOGGER.info("Login attempt. Username: `{}`. Status: `{}`", username, COMPLETED);

        this.sessionRegistry.register(
                new Session(
                        username,
                        new JwtRefreshToken(jwtRefreshToken.value())
                )
        );

        return this.currentSessionAssistant.getCurrentClientUser();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request, HttpServletResponse response) throws CookieRefreshTokenNotFoundException {
        var cookieRefreshToken = this.cookieProvider.readJwtRefreshToken(request);
        var refreshToken = cookieRefreshToken.value();
        if (nonNull(refreshToken)) {
            var jwtRefreshToken = cookieRefreshToken.getJwtRefreshToken();
            var validatedClaims = this.securityJwtTokenUtility.validate(jwtRefreshToken);
            if (validatedClaims.valid()) {
                var username = validatedClaims.safeGetUsername();
                this.sessionRegistry.logout(
                        new Session(
                                username,
                                jwtRefreshToken
                        )
                );

                this.cookieProvider.clearCookies(response);
                SecurityContextHolder.clearContext();
                var session = request.getSession(false);
                if (nonNull(session)) {
                    session.invalidate();
                }
                LOGGER.debug("Logout attempt completed successfully. Username: {}", username);
            }
        }
    }

    @PostMapping("/refreshToken")
    @ResponseStatus(HttpStatus.OK)
    public ResponseUserSession1 refreshToken(HttpServletRequest request, HttpServletResponse response) throws CookieUnauthorizedException {
        try {
            return this.tokenService.refreshSessionOrThrow(request, response);
        } catch (CookieRefreshTokenNotFoundException | CookieRefreshTokenInvalidException | CookieRefreshTokenExpiredException | CookieRefreshTokenDbNotFoundException ex) {
            this.cookieProvider.clearCookies(response);
            throw new CookieUnauthorizedException(ex.getMessage());
        }
    }
}
