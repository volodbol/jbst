package io.tech1.framework.b2b.base.security.jwt.websockets.template.impl;

import io.tech1.framework.b2b.base.security.jwt.websockets.domain.events.WebsocketEvent;
import io.tech1.framework.b2b.base.security.jwt.websockets.tempate.WssMessagingTemplate;
import io.tech1.framework.b2b.base.security.jwt.websockets.tempate.impl.WssMessagingTemplateImpl;
import io.tech1.framework.domain.base.Username;
import io.tech1.framework.incidents.domain.throwable.IncidentThrowable;
import io.tech1.framework.incidents.events.publishers.IncidentPublisher;
import io.tech1.framework.properties.ApplicationFrameworkProperties;
import io.tech1.framework.properties.tests.contexts.ApplicationFrameworkPropertiesContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static io.tech1.framework.domain.utilities.random.RandomUtility.randomString;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class, MockitoExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class WssMessagingTemplateImplTest {

    @Configuration
    @Import({
            ApplicationFrameworkPropertiesContext.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final ApplicationFrameworkProperties applicationFrameworkProperties;

        @Bean
        SimpMessagingTemplate simpMessagingTemplate() {
            return mock(SimpMessagingTemplate.class);
        }

        @Bean
        IncidentPublisher serverIncidentPublisher() {
            return mock(IncidentPublisher.class);
        }

        @Bean
        WssMessagingTemplate wssMessagingTemplate() {
            return new WssMessagingTemplateImpl(
                    this.simpMessagingTemplate(),
                    this.serverIncidentPublisher(),
                    this.applicationFrameworkProperties
            );
        }
    }

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final IncidentPublisher incidentPublisher;
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    private final WssMessagingTemplate componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.simpMessagingTemplate,
                this.incidentPublisher
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.simpMessagingTemplate,
                this.incidentPublisher
        );
    }

    @Test
    void convertAndSendToUserThrowExceptionTest() {
        // Assert
        var username = Username.random();
        var destination = randomString();
        var websocketEvent = mock(WebsocketEvent.class);
        var ex = new MessagingException(randomString());
        var simpleDestination = this.applicationFrameworkProperties.getSecurityJwtWebsocketsConfigs().getBrokerConfigs().getSimpleDestination();
        doThrow(ex).when(this.simpMessagingTemplate).convertAndSendToUser(username.identifier(), simpleDestination + destination, websocketEvent);

        // Act
        this.componentUnderTest.sendEventToUser(username, destination, websocketEvent);

        // Assert
        verify(this.simpMessagingTemplate).convertAndSendToUser(username.identifier(), simpleDestination + destination, websocketEvent);
        verify(this.incidentPublisher).publishThrowable(IncidentThrowable.of(ex));
        verifyNoMoreInteractions(this.simpMessagingTemplate);
    }

    @Test
    void convertAndSendToUserTest() {
        // Assert
        var username = Username.random();
        var destination = randomString();
        var websocketEvent = mock(WebsocketEvent.class);
        var simpleDestination = this.applicationFrameworkProperties.getSecurityJwtWebsocketsConfigs().getBrokerConfigs().getSimpleDestination();

        // Act
        this.componentUnderTest.sendEventToUser(username, destination, websocketEvent);

        // Assert
        verify(this.simpMessagingTemplate).convertAndSendToUser(username.identifier(), simpleDestination + destination, websocketEvent);
        verifyNoMoreInteractions(this.simpMessagingTemplate);
    }
}