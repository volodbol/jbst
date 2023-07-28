package io.tech1.framework.b2b.mongodb.security.jwt.sessions;

import io.tech1.framework.b2b.base.security.jwt.domain.dto.responses.ResponseUserSession2;
import io.tech1.framework.b2b.base.security.jwt.domain.events.EventAuthenticationLogin;
import io.tech1.framework.b2b.base.security.jwt.domain.events.EventAuthenticationLogout;
import io.tech1.framework.b2b.base.security.jwt.domain.events.EventSessionExpired;
import io.tech1.framework.b2b.base.security.jwt.domain.events.EventSessionRefreshed;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.CookieRefreshToken;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtRefreshToken;
import io.tech1.framework.b2b.base.security.jwt.domain.sessions.Session;
import io.tech1.framework.b2b.base.security.jwt.domain.sessions.SessionsValidatedTuple2;
import io.tech1.framework.b2b.base.security.jwt.events.publishers.SecurityJwtIncidentPublisher;
import io.tech1.framework.b2b.base.security.jwt.events.publishers.SecurityJwtPublisher;
import io.tech1.framework.b2b.base.security.jwt.sessions.SessionRegistry;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.db.MongoDbUserSession;
import io.tech1.framework.b2b.mongodb.security.jwt.repositories.MongoUserSessionsRepository;
import io.tech1.framework.b2b.mongodb.security.jwt.services.UserSessionService;
import io.tech1.framework.domain.base.Username;
import io.tech1.framework.domain.http.requests.UserRequestMetadata;
import io.tech1.framework.domain.tuples.Tuple2;
import io.tech1.framework.domain.tuples.Tuple3;
import io.tech1.framework.incidents.domain.authetication.IncidentAuthenticationLogoutFull;
import io.tech1.framework.incidents.domain.authetication.IncidentAuthenticationLogoutMin;
import io.tech1.framework.incidents.domain.session.IncidentSessionExpired;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static io.tech1.framework.domain.http.requests.UserRequestMetadata.processed;
import static io.tech1.framework.domain.utilities.random.EntityUtility.entity;
import static io.tech1.framework.domain.utilities.random.RandomUtility.*;
import static io.tech1.framework.domain.utilities.reflections.ReflectionUtility.setPrivateField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class MongoSessionRegistryTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        SecurityJwtPublisher securityJwtPublisher() {
            return mock(SecurityJwtPublisher.class);
        }

        @Bean
        SecurityJwtIncidentPublisher securityJwtIncidentPublisher() {
            return mock(SecurityJwtIncidentPublisher.class);
        }

        @Bean
        UserSessionService userSessionService() {
            return mock(UserSessionService.class);
        }

        @Bean
        MongoUserSessionsRepository mongoUserSessionsRepository() {
            return mock(MongoUserSessionsRepository.class);
        }

        @Bean
        SessionRegistry sessionRegistry() {
            return new MongoSessionRegistry(
                    this.securityJwtPublisher(),
                    this.securityJwtIncidentPublisher(),
                    this.userSessionService(),
                    this.mongoUserSessionsRepository()
            );
        }
    }

    private final SecurityJwtPublisher securityJwtPublisher;
    private final SecurityJwtIncidentPublisher securityJwtIncidentPublisher;
    private final UserSessionService userSessionService;
    private final MongoUserSessionsRepository mongoUserSessionsRepository;

    private final SessionRegistry componentUnderTest;

    @BeforeEach
    void beforeEach() throws Exception {
        // WARNING: clean session to execute a.k.a. integration test -> method "integrationFlow"
        setPrivateField(this.componentUnderTest, "sessions", ConcurrentHashMap.newKeySet());
        reset(
                this.securityJwtIncidentPublisher,
                this.securityJwtPublisher,
                this.userSessionService,
                this.mongoUserSessionsRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.securityJwtIncidentPublisher,
                this.securityJwtPublisher,
                this.userSessionService,
                this.mongoUserSessionsRepository
        );
    }

    @Test
    void integrationTest() {
        // Arrange
        var session1 = new Session(Username.of("username1"), new JwtRefreshToken(randomString()));
        var session2 = new Session(Username.of("username2"), new JwtRefreshToken(randomString()));
        var session3 = new Session(Username.of("username3"), new JwtRefreshToken(randomString()));
        var session4 = new Session(Username.of("username4"), new JwtRefreshToken(randomString()));
        var rndSession = new Session(randomUsername(), new JwtRefreshToken(randomString()));
        var dbUserSession1 = entity(MongoDbUserSession.class);
        var dbUserSession2 = entity(MongoDbUserSession.class);
        var dbUserSession3 = entity(MongoDbUserSession.class);
        var dbUserSession4 = entity(MongoDbUserSession.class);
        var rndDbUserSession = entity(MongoDbUserSession.class);
        when(this.mongoUserSessionsRepository.findByRefreshToken(session1.refreshToken())).thenReturn(dbUserSession1);
        when(this.mongoUserSessionsRepository.findByRefreshToken(session2.refreshToken())).thenReturn(dbUserSession2);
        when(this.mongoUserSessionsRepository.findByRefreshToken(session3.refreshToken())).thenReturn(dbUserSession3);
        when(this.mongoUserSessionsRepository.findByRefreshToken(session4.refreshToken())).thenReturn(dbUserSession4);
        when(this.mongoUserSessionsRepository.findByRefreshToken(rndSession.refreshToken())).thenReturn(rndDbUserSession);

        // Iteration #1
        var activeSessionsUsernames1 = this.componentUnderTest.getActiveSessionsUsernamesIdentifiers();
        assertThat(activeSessionsUsernames1).isEmpty();
        assertThat(this.componentUnderTest.getActiveSessionsRefreshTokens()).isEmpty();

        // Iteration #2
        this.componentUnderTest.register(session1);
        this.componentUnderTest.register(session2);
        var activeSessionsUsernames2 = this.componentUnderTest.getActiveSessionsUsernames();
        assertThat(activeSessionsUsernames2).hasSize(2);
        assertThat(this.componentUnderTest.getActiveSessionsRefreshTokens()).hasSize(2);
        assertThat(activeSessionsUsernames2).isEqualTo(new HashSet<>(List.of(session1.username(), session2.username())));

        // Iteration #3
        this.componentUnderTest.register(session3);
        this.componentUnderTest.logout(rndSession);
        var activeSessionsUsernames3 = this.componentUnderTest.getActiveSessionsUsernames();
        assertThat(activeSessionsUsernames3).hasSize(3);
        assertThat(this.componentUnderTest.getActiveSessionsRefreshTokens()).hasSize(3);
        assertThat(activeSessionsUsernames3).isEqualTo(new HashSet<>(List.of(session1.username(), session2.username(), session3.username())));

        // Iteration #4
        this.componentUnderTest.register(session4);
        this.componentUnderTest.logout(session1);
        this.componentUnderTest.logout(session2);
        this.componentUnderTest.logout(session3);
        var activeSessionsUsernames4 = this.componentUnderTest.getActiveSessionsUsernames();
        assertThat(activeSessionsUsernames4).hasSize(1);
        assertThat(this.componentUnderTest.getActiveSessionsRefreshTokens()).hasSize(1);
        assertThat(activeSessionsUsernames4).isEqualTo(new HashSet<>(List.of(session4.username())));

        // Iteration #5 (cleanup)
        this.componentUnderTest.logout(session4);
        assertThat(this.componentUnderTest.getActiveSessionsUsernamesIdentifiers()).isEmpty();
        assertThat(this.componentUnderTest.getActiveSessionsUsernames()).isEmpty();
        assertThat(this.componentUnderTest.getActiveSessionsRefreshTokens()).isEmpty();
        verify(this.mongoUserSessionsRepository).findByRefreshToken(session1.refreshToken());
        verify(this.mongoUserSessionsRepository).findByRefreshToken(session2.refreshToken());
        verify(this.mongoUserSessionsRepository).findByRefreshToken(session3.refreshToken());
        verify(this.mongoUserSessionsRepository).findByRefreshToken(session4.refreshToken());
        verify(this.mongoUserSessionsRepository).findByRefreshToken(rndSession.refreshToken());
        verify(this.securityJwtPublisher).publishAuthenticationLogin(new EventAuthenticationLogin(session1.username()));
        verify(this.securityJwtPublisher).publishAuthenticationLogin(new EventAuthenticationLogin(session2.username()));
        verify(this.securityJwtPublisher).publishAuthenticationLogin(new EventAuthenticationLogin(session3.username()));
        verify(this.securityJwtPublisher).publishAuthenticationLogin(new EventAuthenticationLogin(session4.username()));
        verify(this.securityJwtPublisher).publishAuthenticationLogout(new EventAuthenticationLogout(session1));
        verify(this.securityJwtPublisher).publishAuthenticationLogout(new EventAuthenticationLogout(session2));
        verify(this.securityJwtPublisher).publishAuthenticationLogout(new EventAuthenticationLogout(session3));
        verify(this.securityJwtPublisher).publishAuthenticationLogout(new EventAuthenticationLogout(session4));
        verify(this.securityJwtPublisher).publishAuthenticationLogout(new EventAuthenticationLogout(rndSession));
        verify(this.securityJwtIncidentPublisher).publishAuthenticationLogoutFull(new IncidentAuthenticationLogoutFull(session1.username(), dbUserSession1.getRequestMetadata()));
        verify(this.securityJwtIncidentPublisher).publishAuthenticationLogoutFull(new IncidentAuthenticationLogoutFull(session2.username(), dbUserSession2.getRequestMetadata()));
        verify(this.securityJwtIncidentPublisher).publishAuthenticationLogoutFull(new IncidentAuthenticationLogoutFull(session3.username(), dbUserSession3.getRequestMetadata()));
        verify(this.securityJwtIncidentPublisher).publishAuthenticationLogoutFull(new IncidentAuthenticationLogoutFull(session4.username(), dbUserSession4.getRequestMetadata()));
        verify(this.securityJwtIncidentPublisher).publishAuthenticationLogoutFull(new IncidentAuthenticationLogoutFull(rndSession.username(), rndDbUserSession.getRequestMetadata()));
        verify(this.mongoUserSessionsRepository).deleteByRefreshToken(session1.refreshToken());
        verify(this.mongoUserSessionsRepository).deleteByRefreshToken(session2.refreshToken());
        verify(this.mongoUserSessionsRepository).deleteByRefreshToken(session3.refreshToken());
        verify(this.mongoUserSessionsRepository).deleteByRefreshToken(session4.refreshToken());
        verify(this.mongoUserSessionsRepository).deleteByRefreshToken(rndSession.refreshToken());
    }

    @Test
    void registerTest() {
        // Arrange
        var username = Username.of("incident");

        // Act
        this.componentUnderTest.register(new Session(username, entity(JwtRefreshToken.class)));
        this.componentUnderTest.register(new Session(username, entity(JwtRefreshToken.class)));

        var duplicatedJwtRefreshToken = entity(JwtRefreshToken.class);
        this.componentUnderTest.register(new Session(username, duplicatedJwtRefreshToken));
        this.componentUnderTest.register(new Session(username, duplicatedJwtRefreshToken));
        this.componentUnderTest.register(new Session(username, duplicatedJwtRefreshToken));

        // Assert
        assertThat(this.componentUnderTest.getActiveSessionsUsernamesIdentifiers()).hasSize(1);
        assertThat(this.componentUnderTest.getActiveSessionsUsernames()).hasSize(1);
        verify(this.securityJwtPublisher, times(3)).publishAuthenticationLogin(new EventAuthenticationLogin(username));
    }

    @Test
    void renewTest() {
        // Arrange
        var username = Username.of("incident");

        // Act
        this.componentUnderTest.renew(new Session(username, entity(JwtRefreshToken.class)), new Session(username, entity(JwtRefreshToken.class)));
        this.componentUnderTest.renew(new Session(username, entity(JwtRefreshToken.class)), new Session(username, entity(JwtRefreshToken.class)));

        var duplicatedJwtRefreshToken = entity(JwtRefreshToken.class);
        this.componentUnderTest.renew(new Session(username, entity(JwtRefreshToken.class)), new Session(username, duplicatedJwtRefreshToken));
        this.componentUnderTest.renew(new Session(username, entity(JwtRefreshToken.class)), new Session(username, duplicatedJwtRefreshToken));
        this.componentUnderTest.renew(new Session(username, entity(JwtRefreshToken.class)), new Session(username, duplicatedJwtRefreshToken));

        // Assert
        assertThat(this.componentUnderTest.getActiveSessionsUsernamesIdentifiers()).hasSize(1);
        assertThat(this.componentUnderTest.getActiveSessionsUsernames()).hasSize(1);
        verify(this.securityJwtPublisher, times(3)).publishSessionRefreshed(any(EventSessionRefreshed.class));
    }

    @Test
    void logoutDbUserSessionPresentTest() {
        // Arrange
        var username = Username.of("incident");
        var refreshToken = entity(JwtRefreshToken.class);
        var session = new Session(username, refreshToken);
        var dbUserSession = entity(MongoDbUserSession.class);
        when(this.mongoUserSessionsRepository.findByRefreshToken(refreshToken)).thenReturn(dbUserSession);

        // Act
        this.componentUnderTest.logout(session);

        // Assert
        verify(this.mongoUserSessionsRepository).findByRefreshToken(refreshToken);
        var eventAC = ArgumentCaptor.forClass(EventAuthenticationLogout.class);
        verify(this.securityJwtPublisher).publishAuthenticationLogout(eventAC.capture());
        verify(this.securityJwtPublisher).publishAuthenticationLogout(eventAC.capture());
        var incidentAC = ArgumentCaptor.forClass(IncidentAuthenticationLogoutFull.class);
        verify(this.securityJwtIncidentPublisher).publishAuthenticationLogoutFull(incidentAC.capture());
        var incident = incidentAC.getValue();
        assertThat(incident.username()).isEqualTo(username);
        assertThat(incident.userRequestMetadata()).isEqualTo(dbUserSession.getRequestMetadata());
        verify(this.mongoUserSessionsRepository).deleteByRefreshToken(refreshToken);
    }

    @Test
    void logoutDbUserSessionNotPresentTest() {
        // Arrange
        var username = Username.of("incident");
        var refreshToken = entity(JwtRefreshToken.class);
        var session = new Session(username, refreshToken);
        when(this.mongoUserSessionsRepository.findByRefreshToken(refreshToken)).thenReturn(null);

        // Act
        this.componentUnderTest.logout(session);

        // Assert
        verify(this.mongoUserSessionsRepository).findByRefreshToken(refreshToken);
        var eventAC = ArgumentCaptor.forClass(EventAuthenticationLogout.class);
        verify(this.securityJwtPublisher).publishAuthenticationLogout(eventAC.capture());
        assertThat(eventAC.getValue().session()).isEqualTo(session);
        var incidentAC = ArgumentCaptor.forClass(IncidentAuthenticationLogoutMin.class);
        verify(this.securityJwtIncidentPublisher).publishAuthenticationLogoutMin(incidentAC.capture());
        assertThat(incidentAC.getValue().username()).isEqualTo(username);
    }

    @Test
    void cleanByExpiredRefreshTokensEnabledTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        var username1 = Username.of("username1");
        var username2 = Username.of("username2");
        var username3 = Username.of("username3");
        var session1 = new Session(username1, entity(JwtRefreshToken.class));
        var session2 = new Session(username2, entity(JwtRefreshToken.class));
        var session3 = new Session(username3, entity(JwtRefreshToken.class));
        Set<Session> sessions = ConcurrentHashMap.newKeySet();
        sessions.add(session1);
        sessions.add(session2);
        sessions.add(session3);
        setPrivateField(this.componentUnderTest, "sessions", sessions);
        var dbUserSession1 = mock(MongoDbUserSession.class);
        when(dbUserSession1.getId()).thenReturn(randomString());
        when(dbUserSession1.getRequestMetadata()).thenReturn(entity(UserRequestMetadata.class));
        var dbUserSession2 = mock(MongoDbUserSession.class);
        when(dbUserSession2.getId()).thenReturn(randomString());
        when(dbUserSession2.getRequestMetadata()).thenReturn(entity(UserRequestMetadata.class));
        var dbUserSession3 = mock(MongoDbUserSession.class);
        when(dbUserSession3.getId()).thenReturn(randomString());
        when(dbUserSession3.getRequestMetadata()).thenReturn(entity(UserRequestMetadata.class));
        var usersSessions = List.of(dbUserSession1, dbUserSession2, dbUserSession3);
        var sessionsValidatedTuple2 = new SessionsValidatedTuple2(
                List.of(new Tuple3<>(username1, dbUserSession3.getRequestMetadata(), dbUserSession3.getJwtRefreshToken())),
                List.of(dbUserSession1.getId(), dbUserSession2.getId())
        );
        var usernames = Set.of(username1, username2, username3);
        when(this.mongoUserSessionsRepository.findByUsernameIn(usernames)).thenReturn(usersSessions);
        when(this.userSessionService.validate(usersSessions)).thenReturn(sessionsValidatedTuple2);

        // Act
        this.componentUnderTest.cleanByExpiredRefreshTokens(usernames);

        // Assert
        verify(this.mongoUserSessionsRepository).findByUsernameIn(usernames);
        verify(this.userSessionService).validate(usersSessions);
        assertThat(this.componentUnderTest.getActiveSessionsUsernamesIdentifiers()).hasSize(3);
        assertThat(this.componentUnderTest.getActiveSessionsUsernames()).hasSize(3);
        var eseCaptor = ArgumentCaptor.forClass(EventSessionExpired.class);
        verify(this.securityJwtPublisher).publishSessionExpired(eseCaptor.capture());
        var eventSessionExpired = eseCaptor.getValue();
        assertThat(eventSessionExpired.session().username()).isEqualTo(username1);
        assertThat(eventSessionExpired.session().refreshToken()).isEqualTo(dbUserSession3.getJwtRefreshToken());
        var seiCaptor = ArgumentCaptor.forClass(IncidentSessionExpired.class);
        verify(this.securityJwtIncidentPublisher).publishSessionExpired(seiCaptor.capture());
        var sessionExpiredIncident = seiCaptor.getValue();
        assertThat(sessionExpiredIncident.username()).isEqualTo(username1);
        assertThat(sessionExpiredIncident.userRequestMetadata()).isEqualTo(dbUserSession3.getRequestMetadata());
        verify(this.mongoUserSessionsRepository).deleteByIdIn(List.of(dbUserSession1.getId(), dbUserSession2.getId()));
    }

    @Test
    void getSessionsTableTest() {
        // Arrange
        var username = entity(Username.class);
        var validJwtRefreshToken = randomString();
        var cookie = new CookieRefreshToken(validJwtRefreshToken);

        Function<Tuple2<UserRequestMetadata, String>, MongoDbUserSession> sessionFnc =
                tuple2 -> new MongoDbUserSession(new JwtRefreshToken(tuple2.b()), randomUsername(), tuple2.a());

        var validSession = sessionFnc.apply(new Tuple2<>(processed(validGeoLocation(), validUserAgentDetails()), validJwtRefreshToken));
        var invalidSession1 = sessionFnc.apply(new Tuple2<>(processed(invalidGeoLocation(), validUserAgentDetails()), randomString()));
        var invalidSession2 = sessionFnc.apply(new Tuple2<>(processed(validGeoLocation(), invalidUserAgentDetails()), randomString()));
        var invalidSession3 = sessionFnc.apply(new Tuple2<>(processed(invalidGeoLocation(), invalidUserAgentDetails()), randomString()));

        // userSessions, expectedSessionSize, expectedAnyProblems
        List<Tuple3<List<MongoDbUserSession>, Integer, Boolean>> cases = new ArrayList<>();
        cases.add(
                new Tuple3<>(
                        List.of(validSession),
                        1,
                        false
                )
        );
        cases.add(
                new Tuple3<>(
                        List.of(validSession, invalidSession1),
                        2,
                        true
                )
        );
        cases.add(
                new Tuple3<>(
                        List.of(validSession, invalidSession1, invalidSession2),
                        3,
                        true
                )
        );
        cases.add(
                new Tuple3<>(
                        List.of(validSession, invalidSession1, invalidSession2, invalidSession3),
                        4,
                        true
                )
        );

        // Act
        cases.forEach(item -> {
            // Arrange
            var userSessions = item.a();
            var expectedSessionSize = item.b();
            var expectedAnyProblems = item.c();
            when(this.mongoUserSessionsRepository.findByUsername(username)).thenReturn(userSessions);

            // Act
            var currentUserDbSessionsTable = this.componentUnderTest.getSessionsTable(username, cookie);

            // Assert
            verify(this.mongoUserSessionsRepository).findByUsername(username);
            assertThat(currentUserDbSessionsTable).isNotNull();
            assertThat(currentUserDbSessionsTable.sessions()).hasSize(expectedSessionSize);
            assertThat(currentUserDbSessionsTable.sessions().stream().filter(ResponseUserSession2::current).count()).isEqualTo(1);
            assertThat(currentUserDbSessionsTable.sessions().stream().filter(session -> "Current session".equals(session.activity())).count()).isEqualTo(1);
            assertThat(currentUserDbSessionsTable.anyProblem()).isEqualTo(expectedAnyProblems);

            reset(
                    this.mongoUserSessionsRepository
            );
        });
    }
}
