package io.tech1.framework.b2b.base.security.jwt.repositories;

import io.tech1.framework.b2b.base.security.jwt.domain.db.AnyDbUserSession;
import io.tech1.framework.b2b.base.security.jwt.domain.dto.responses.ResponseUserSession2;
import io.tech1.framework.b2b.base.security.jwt.domain.identifiers.UserSessionId;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.CookieAccessToken;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtAccessToken;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtRefreshToken;
import io.tech1.framework.domain.base.Username;
import io.tech1.framework.domain.tuples.Tuple2;
import io.tech1.framework.domain.tuples.TuplePresence;

import java.util.List;
import java.util.Set;

// TODO [YY] add unit tests
public interface AnyDbUsersSessionsRepository {
    TuplePresence<AnyDbUserSession> isPresent(UserSessionId userSessionId);
    TuplePresence<AnyDbUserSession> isPresent(JwtAccessToken accessToken);
    TuplePresence<AnyDbUserSession> isPresent(JwtRefreshToken refreshToken);
    List<ResponseUserSession2> findByUsernameAndCookieAsSession2(Username username, CookieAccessToken cookie);
    List<Tuple2<ResponseUserSession2, JwtAccessToken>> findAllByCookieAsSession2(CookieAccessToken cookie);
    List<AnyDbUserSession> findByUsernameInAsAny(Set<Username> usernames);
    UserSessionId saveAs(AnyDbUserSession userSession);
    void delete(UserSessionId sessionId);
    long deleteByUsersSessionsIds(List<UserSessionId> sessionsIds);
    void deleteByUsernameExceptAccessToken(Username username, CookieAccessToken cookie);
    void deleteExceptAccessToken(CookieAccessToken cookie);
}
