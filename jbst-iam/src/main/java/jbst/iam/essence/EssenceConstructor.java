package jbst.iam.essence;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tech1.framework.foundation.domain.properties.base.DefaultUser;

import java.util.List;
import java.util.Set;

public interface EssenceConstructor {
    long saveDefaultUsers(List<DefaultUser> defaultUsers);
    void saveInvitationCodes(DefaultUser defaultUser, Set<SimpleGrantedAuthority> authorities);
}
