package io.tech1.framework.domain.http.requests;

import io.tech1.framework.domain.utilities.random.RandomUtility;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserAgentHeaderTest {

    @Test
    public void constructorsRequestNull() {
        // Act
        var actual = new UserAgentHeader(null);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo("");
    }

    @Test
    public void constructorsRequestNoHeader() {
        // Arrange
        var request = mock(HttpServletRequest.class);

        // Act
        var actual = new UserAgentHeader(request);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo("");
    }

    @Test
    public void constructorsRequestValid() {
        // Arrange
        var userAgentHeader = RandomUtility.randomString();
        var request = mock(HttpServletRequest.class);
        when(request.getHeader("User-Agent")).thenReturn(userAgentHeader);

        // Act
        var actual = new UserAgentHeader(request);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo(userAgentHeader);
    }
}
