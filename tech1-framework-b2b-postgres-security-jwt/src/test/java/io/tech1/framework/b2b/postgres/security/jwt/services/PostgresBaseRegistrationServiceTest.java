package io.tech1.framework.b2b.postgres.security.jwt.services;

import io.tech1.framework.b2b.base.security.jwt.domain.dto.requests.RequestUserRegistration1;
import io.tech1.framework.b2b.base.security.jwt.services.BaseRegistrationService;
import io.tech1.framework.b2b.postgres.security.jwt.domain.db.PostgresDbInvitationCode;
import io.tech1.framework.b2b.postgres.security.jwt.domain.db.PostgresDbUser;
import io.tech1.framework.b2b.postgres.security.jwt.repositories.PostgresInvitationCodesRepository;
import io.tech1.framework.b2b.postgres.security.jwt.repositories.PostgresUsersRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static io.tech1.framework.domain.utilities.random.EntityUtility.entity;
import static io.tech1.framework.domain.utilities.random.RandomUtility.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PostgresBaseRegistrationServiceTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        PostgresInvitationCodesRepository invitationCodeRepository() {
            return mock(PostgresInvitationCodesRepository.class);
        }

        @Bean
        PostgresUsersRepository userRepository() {
            return mock(PostgresUsersRepository.class);
        }

        @Bean
        BCryptPasswordEncoder bCryptPasswordEncoder() {
            return mock(BCryptPasswordEncoder.class);
        }

        @Bean
        BaseRegistrationService registrationService() {
            return new PostgresBaseRegistrationService(
                    this.invitationCodeRepository(),
                    this.userRepository(),
                    this.bCryptPasswordEncoder()
            );
        }
    }

    private final PostgresInvitationCodesRepository invitationCodesRepository;
    private final PostgresUsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final BaseRegistrationService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.invitationCodesRepository,
                this.usersRepository,
                this.bCryptPasswordEncoder
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.invitationCodesRepository,
                this.usersRepository,
                this.bCryptPasswordEncoder
        );
    }

    @Test
    void register1Test() {
        // Arrange
        var requestUserRegistration1 = new RequestUserRegistration1(
                randomUsername(),
                randomPassword(),
                randomPassword(),
                randomZoneId().getId(),
                randomString()
        );
        var dbInvitationCode = entity(PostgresDbInvitationCode.class);
        when(this.invitationCodesRepository.findByValue(requestUserRegistration1.invitationCode())).thenReturn(dbInvitationCode);
        var hashPassword = randomString();
        when(this.bCryptPasswordEncoder.encode(requestUserRegistration1.password().value())).thenReturn(hashPassword);
        var dbUserAC = ArgumentCaptor.forClass(PostgresDbUser.class);
        var dbInvitationCodeAC = ArgumentCaptor.forClass(PostgresDbInvitationCode.class);

        // Act
        this.componentUnderTest.register1(requestUserRegistration1);

        // Assert
        verify(this.invitationCodesRepository).findByValue(requestUserRegistration1.invitationCode());
        verify(this.bCryptPasswordEncoder).encode(requestUserRegistration1.password().value());
        verify(this.usersRepository).save(dbUserAC.capture());
        assertThat(dbUserAC.getValue().getUsername()).isEqualTo(requestUserRegistration1.username());
        assertThat(dbUserAC.getValue().getPassword().value()).isEqualTo(hashPassword);
        assertThat(dbUserAC.getValue().getAuthorities()).isEqualTo(dbInvitationCode.getAuthorities());
        verify(this.invitationCodesRepository).save(dbInvitationCodeAC.capture());
        assertThat(dbInvitationCodeAC.getValue().getInvited()).isEqualTo(requestUserRegistration1.username());
    }
}
