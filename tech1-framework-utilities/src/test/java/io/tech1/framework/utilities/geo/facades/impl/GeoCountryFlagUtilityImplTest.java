package io.tech1.framework.utilities.geo.facades.impl;

import io.tech1.framework.properties.ApplicationFrameworkProperties;
import io.tech1.framework.properties.tests.contexts.ApplicationFrameworkPropertiesContext;
import io.tech1.framework.utilities.geo.facades.GeoCountryFlagUtility;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Stream;

import static io.tech1.framework.domain.constants.StringConstants.UNDEFINED;
import static io.tech1.framework.domain.constants.StringConstants.UNKNOWN;
import static io.tech1.framework.domain.tests.constants.TestsFlagsConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GeoCountryFlagUtilityImplTest {

    private static Stream<Arguments> getFlagEmojiTest() {
        return Stream.of(
                Arguments.of(null, null, FLAG_UNKNOWN),
                Arguments.of("Ukraine", "UA", FLAG_UKRAINE),
                Arguments.of("Portugal", "PT", FLAG_PORTUGAL),
                Arguments.of("United States", "US", FLAG_USA),
                Arguments.of(UNKNOWN, UNKNOWN, FLAG_UNKNOWN),
                Arguments.of(UNDEFINED, UNDEFINED, FLAG_UNKNOWN)
        );
    }

    @Configuration
    @Import({
            ApplicationFrameworkPropertiesContext.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final ResourceLoader resourceLoader;
        private final ApplicationFrameworkProperties applicationFrameworkProperties;

        @Bean
        GeoCountryFlagUtility geoCountryFlagUtility() {
            return new GeoCountryFlagUtilityImpl(
                    this.resourceLoader,
                    this.applicationFrameworkProperties
            );
        }
    }

    private final GeoCountryFlagUtility componentUnderTest;

    @ParameterizedTest
    @MethodSource("getFlagEmojiTest")
    void getFlagEmojiTest(String country, String countryCode, String expected) {
        // Act
        var actual1 = this.componentUnderTest.getFlagEmojiByCountry(country);
        var actual2 = this.componentUnderTest.getFlagEmojiByCountryCode(countryCode);

        // Assert
        assertThat(actual1).isEqualTo(expected);
        assertThat(actual2).isEqualTo(expected);
    }
}
