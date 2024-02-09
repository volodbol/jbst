package io.tech1.framework.domain.properties.base;

import io.tech1.framework.domain.base.Password;
import io.tech1.framework.domain.base.Username;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.time.ZoneId.systemDefault;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultUsersTest {

    private static Stream<Arguments> getDefaultUsersAuthoritiesTest() {
        return Stream.of(
                Arguments.of(null, emptySet()),
                Arguments.of(
                        List.of(
                                new DefaultUser(Username.of("user1"), Password.of("pass1"), systemDefault(), null, null)
                        ),
                        emptySet()
                ),
                Arguments.of(
                        List.of(
                                new DefaultUser(Username.of("user1"), Password.of("pass1"), systemDefault(), null, Set.of("user")),
                                new DefaultUser(Username.of("user2"), Password.of("pass2"), systemDefault(), null, Set.of("admin", "user"))
                        ),
                        Set.of("user", "admin")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getDefaultUsersAuthoritiesTest")
    void getDefaultUsersAuthoritiesTest(List<DefaultUser> users, Set<String> expected) {
        // Arrange
        var defaultUsers = new DefaultUsers(true, users);

        // Act
       var actual = defaultUsers.getDefaultUsersAuthorities();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
