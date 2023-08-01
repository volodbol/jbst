package io.tech1.framework.b2b.base.security.jwt.resources;

import io.tech1.framework.b2b.base.security.jwt.cookies.CookieProvider;
import io.tech1.framework.b2b.base.security.jwt.domain.dto.responses.ResponseInvitationCode;
import io.tech1.framework.b2b.base.security.jwt.domain.dto.responses.ResponseServerSessionsTable;
import io.tech1.framework.b2b.base.security.jwt.domain.dto.responses.ResponseUserSession2;
import io.tech1.framework.b2b.base.security.jwt.domain.identifiers.UserSessionId;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.CookieAccessToken;
import io.tech1.framework.b2b.base.security.jwt.services.BaseSuperAdminService;
import io.tech1.framework.b2b.base.security.jwt.services.BaseUsersSessionsService;
import io.tech1.framework.b2b.base.security.jwt.tests.runners.AbstractResourcesRunner;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;

import static io.tech1.framework.domain.utilities.random.EntityUtility.entity;
import static io.tech1.framework.domain.utilities.random.EntityUtility.list345;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BaseSuperAdminResourceTest extends AbstractResourcesRunner {

    // Services
    private final BaseSuperAdminService baseSuperAdminService;
    private final BaseUsersSessionsService baseUsersSessionsService;
    // Cookie
    private final CookieProvider cookieProvider;

    // Resource
    private final BaseSuperAdminResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.baseSuperAdminService,
                this.baseUsersSessionsService,
                this.cookieProvider
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.baseSuperAdminService,
                this.baseUsersSessionsService,
                this.cookieProvider
        );
    }

    @Test
    void getUnusedInvitationCodesTest() throws Exception {
        // Arrange
        var codes = list345(ResponseInvitationCode.class);
        when(this.baseSuperAdminService.findUnused()).thenReturn(codes);

        // Act
        this.mvc.perform(get("/superadmin/invitationCodes/unused").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(codes.size())))
                .andExpect(jsonPath("$.[0].id", notNullValue()))
                .andExpect(jsonPath("$.[0].owner", notNullValue()))
                .andExpect(jsonPath("$.[0].authorities", notNullValue()))
                .andExpect(jsonPath("$.[0].value", notNullValue()))
                .andExpect(jsonPath("$.[0].invited", notNullValue()));

        // Assert
        verify(this.baseSuperAdminService).findUnused();
    }

    @Test
    void getServerSessions() throws Exception {
        // Arrange
        var sessionsTable = ResponseServerSessionsTable.of(
                list345(ResponseUserSession2.class),
                list345(ResponseUserSession2.class)
        );
        var cookie = entity(CookieAccessToken.class);
        when(this.cookieProvider.readJwtAccessToken(any(HttpServletRequest.class))).thenReturn(cookie);
        when(this.baseSuperAdminService.getServerSessions(cookie)).thenReturn(sessionsTable);

        // Act
        this.mvc.perform(get("/superadmin/sessions").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeSessions", hasSize(sessionsTable.activeSessions().size())))
                .andExpect(jsonPath("$.activeSessions.[0].id", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].who", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].current", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].activity", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].exception", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].ipAddr", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].countryFlag", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].where", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].browser", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].what", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions", hasSize(sessionsTable.inactiveSessions().size())))
                .andExpect(jsonPath("$.inactiveSessions.[0].id", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].who", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].current", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].activity", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].exception", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].ipAddr", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].countryFlag", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].where", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].browser", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].what", notNullValue()));

        // Assert
        verify(this.cookieProvider).readJwtAccessToken(any(HttpServletRequest.class));
        verify(this.baseSuperAdminService).getServerSessions(cookie);
    }

    @Test
    void deleteByIdTest() throws Exception {
        // Arrange
        var sessionId = entity(UserSessionId.class);

        // Act
        this.mvc.perform(
                        delete("/superadmin/sessions/" + sessionId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.baseUsersSessionsService).deleteById(sessionId);
    }

    @Test
    void deleteAllExceptCurrent() throws Exception {
        // Arrange
        var cookie = entity(CookieAccessToken.class);
        when(this.cookieProvider.readJwtAccessToken(any(HttpServletRequest.class))).thenReturn(cookie);

        // Act
        this.mvc.perform(
                        delete("/superadmin/sessions/")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.cookieProvider).readJwtAccessToken(any(HttpServletRequest.class));
        verify(this.baseUsersSessionsService).deleteAllExceptCurrentAsSuperuser(cookie);
    }
}
