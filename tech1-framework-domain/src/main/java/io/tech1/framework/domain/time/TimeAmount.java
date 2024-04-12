package io.tech1.framework.domain.time;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

public record TimeAmount(long amount, ChronoUnit unit) {

    public static TimeAmount testsHardcoded() {
        return new TimeAmount(30L, ChronoUnit.SECONDS);
    }

    public static TimeAmount forever() {
        return new TimeAmount(1L, ChronoUnit.FOREVER);
    }

    @JsonIgnore
    public long toSeconds() {
        return this.amount * this.unit().getDuration().toSeconds();
    }

    @JsonIgnore
    public long toMillis() {
        return this.amount * this.unit().getDuration().toMillis();
    }

    @Target({
            ElementType.FIELD,
            ElementType.METHOD
    })
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = ConstraintValidatorOnTimeAmount.class)
    public @interface ValidTimeAmount {
        String message() default "must be between than {minAmount} {minUnit} and {maxAmount} {maxUnit}";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
        int minAmount();
        ChronoUnit minUnit();
        int maxAmount();
        ChronoUnit maxUnit();
    }

    public static class ConstraintValidatorOnTimeAmount implements ConstraintValidator<TimeAmount.ValidTimeAmount, TimeAmount> {
        private int minAmount;
        private ChronoUnit minUnit;
        private int maxAmount;
        private ChronoUnit maxUnit;

        @Override
        public void initialize(TimeAmount.ValidTimeAmount constraintAnnotation) {
            this.minAmount = constraintAnnotation.minAmount();
            this.minUnit = constraintAnnotation.minUnit();
            this.maxAmount = constraintAnnotation.maxAmount();
            this.maxUnit = constraintAnnotation.maxUnit();
        }

        @Override
        public boolean isValid(TimeAmount configuration, ConstraintValidatorContext constraintValidatorContext) {
            long min = this.minAmount * this.minUnit.getDuration().toMillis();
            long ms = configuration.amount * configuration.unit.getDuration().toMillis();
            long max = this.maxAmount * this.maxUnit.getDuration().toMillis();
            return min <= ms && ms <= max;
        }
    }
}
