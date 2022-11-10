package io.tech1.framework.domain.utilities.random;

import io.tech1.framework.domain.tests.constants.TestsConstants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static io.tech1.framework.domain.utilities.random.RandomUtility.*;
import static java.math.BigDecimal.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

public class RandomUtilityTest {

    private static Stream<Arguments> oneTest() {
        return Stream.of(
                Arguments.of(Short.class, 1),
                Arguments.of(Integer.class, 1),
                Arguments.of(Long.class, 1L),
                Arguments.of(Double.class, 1.0d),
                Arguments.of(BigDecimal.class, ONE)
        );
    }

    @ParameterizedTest
    @MethodSource("oneTest")
    public void oneTest(Class<? extends Number> clazz, Number expected) {
        // Act
        var actual = one(clazz);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void oneTestException() {
        // Act
        var thrown = catchThrowable(() -> one(Float.class));

        // Assert
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertThat(thrown).hasMessageContaining("Unexpected clazz: java.lang.Float");
    }

    @Test
    public void randomShortTest() {
        // Act
        var actual = randomShort();

        // Assert
        assertThat(actual).isNotNull();
    }

    @Test
    public void randomBooleanTest() {
        // Act
        var actual = randomBoolean();

        // Assert
        assertThat(actual).isNotNull();
    }

    @Test
    public void randomDoubleTest() {
        // Act
        var actual = randomDouble();

        // Assert
        assertThat(actual).isNotNull();
    }

    @Test
    public void randomIntegerTest() {
        // Act
        var actual = randomInteger();

        // Assert
        assertThat(actual).isNotNull();
    }

    @RepeatedTest(TestsConstants.RANDOM_ITERATIONS_COUNT)
    public void randomIntegerGreaterThanZeroTest() {
        // Act
        var actual = randomIntegerGreaterThanZero();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual).isPositive();
    }

    @RepeatedTest(TestsConstants.RANDOM_ITERATIONS_COUNT)
    public void randomIntegerLessThanZeroTest() {
        // Act
        var actual = randomIntegerLessThanZero();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual).isNegative();
    }

    @RepeatedTest(TestsConstants.RANDOM_ITERATIONS_COUNT)
    public void randomIntegerGreaterThanZeroByBoundsTest() {
        // Arrange
        var lowerBound = 50;
        var upperBound = 100;

        // Act
        var actual = randomIntegerGreaterThanZeroByBounds(lowerBound, upperBound);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual > lowerBound).isTrue();
        assertThat(actual < upperBound).isTrue();
    }
}
