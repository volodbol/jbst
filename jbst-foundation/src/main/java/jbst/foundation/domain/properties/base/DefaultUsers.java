package jbst.foundation.domain.properties.base;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.random.RandomUtility.randomBoolean;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class DefaultUsers extends AbstractTogglePropertyConfigs {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryToggleProperty
    private List<DefaultUser> users;

    public static DefaultUsers hardcoded() {
        return new DefaultUsers(
                true,
                List.of(
                        new DefaultUser(
                                Username.of("admin12"),
                                Password.of("password12"),
                                ZoneId.systemDefault(),
                                null,
                                false,
                                Set.of("admin")
                        )
                )
        );
    }

    public static DefaultUsers random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static DefaultUsers enabled() {
        return hardcoded();
    }

    public static DefaultUsers disabled() {
        return new DefaultUsers(false, new ArrayList<>());
    }

    public final Set<String> getDefaultUsersAuthorities() {
        if (nonNull(this.users)) {
            return this.users.stream().map(DefaultUser::getAuthorities)
                    .filter(Objects::nonNull)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }
}
