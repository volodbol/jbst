package io.tech1.framework.domain.base;

import com.fasterxml.jackson.core.type.TypeReference;
import io.tech1.framework.domain.constants.DomainConstants;
import io.tech1.framework.domain.tests.runners.AbstractSerializationDeserializationRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTest extends AbstractSerializationDeserializationRunner {
    private static final Email EMAIL = Email.of("info@" + DomainConstants.TECH1);

    @Override
    protected String getFileName() {
        return "email-1.json";
    }

    @Override
    protected String getFolder() {
        return "base";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(EMAIL);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<Email>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(EMAIL);
        assertThat(actual.value()).isEqualTo(EMAIL.value());
        assertThat(actual.toString()).hasToString(EMAIL.value());
    }

    @Test
    void randomEmailTest() {
        // Arrange
        var domain = "@tech1.io";
        var randomLength = 32;
        var domainLength = 9;
        var expected = randomLength + domainLength;

        // Act
        var actual = Email.random();

        // Assert
        assertThat(actual.value()).hasSize(expected);
        assertThat(actual.value().substring(randomLength)).isEqualTo(domain);
    }
}