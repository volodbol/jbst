package io.tech1.framework.incidents.domain.authetication;

import io.tech1.framework.domain.base.Password;
import io.tech1.framework.domain.base.Username;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class IncidentAuthenticationLoginFailureUsernameMaskedPasswordTest {

    private static Stream<Arguments> ofTest() {
        return Stream.of(
                Arguments.of("123456789", "12345****"),
                Arguments.of("12345", "12345"),
                Arguments.of("abcdefghij", "abcde*****"),
                Arguments.of("abc", "abc")
        );
    }

    @ParameterizedTest
    @MethodSource("ofTest")
    void ofTest(String password, String expected) {
        // Arrange
        var username = Username.random();


        // Act
        var actual = new IncidentAuthenticationLoginFailureUsernameMaskedPassword(
                username,
                Password.of(password)
        );

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.username()).isEqualTo(username);
        assertThat(actual.password().value()).isEqualTo(expected);
    }
}