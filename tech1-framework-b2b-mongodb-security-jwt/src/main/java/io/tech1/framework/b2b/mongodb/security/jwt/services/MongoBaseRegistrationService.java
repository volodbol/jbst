package io.tech1.framework.b2b.mongodb.security.jwt.services;

import io.tech1.framework.b2b.base.security.jwt.domain.dto.requests.RequestUserRegistration1;
import io.tech1.framework.b2b.base.security.jwt.services.BaseRegistrationService;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.db.MongoDbUser;
import io.tech1.framework.b2b.mongodb.security.jwt.repositories.MongoInvitationCodesRepository;
import io.tech1.framework.b2b.mongodb.security.jwt.repositories.MongoUsersRepository;
import io.tech1.framework.domain.base.Password;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MongoBaseRegistrationService implements BaseRegistrationService {

    // Repository
    private final MongoInvitationCodesRepository invitationCodesRepository;
    private final MongoUsersRepository usersRepository;
    // Password
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void register1(RequestUserRegistration1 requestUserRegistration1) {
        var invitationCode = invitationCodesRepository.findByValue(requestUserRegistration1.invitationCode());

        var hashPassword = this.bCryptPasswordEncoder.encode(requestUserRegistration1.password().value());

        var user = new MongoDbUser(
                requestUserRegistration1.username(),
                Password.of(hashPassword),
                requestUserRegistration1.zoneId(),
                invitationCode.getAuthorities()
        );

        invitationCode.setInvited(user.getUsername());

        this.usersRepository.save(user);
        this.invitationCodesRepository.save(invitationCode);
    }
}
