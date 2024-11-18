package jbst.iam.services.abstracts;

import jbst.iam.domain.db.Invitation;
import jbst.iam.domain.dto.requests.RequestUserRegistration1;
import jbst.iam.repositories.InvitationCodesRepository;
import jbst.iam.repositories.UsersRepository;
import jbst.iam.services.BaseRegistrationService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jbst.foundation.domain.base.Password;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseRegistrationService implements BaseRegistrationService {

    // Repository
    private final InvitationCodesRepository invitationCodesRepository;
    private final UsersRepository usersRepository;
    // Password
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void register1(RequestUserRegistration1 requestUserRegistration1) {
        var invitationCode = this.invitationCodesRepository.findByValueAsAny(requestUserRegistration1.invitationCode());
        var hashPassword = this.bCryptPasswordEncoder.encode(requestUserRegistration1.password().value());
        invitationCode = new Invitation(
                invitationCode.id(),
                invitationCode.owner(),
                invitationCode.authorities(),
                invitationCode.value(),
                requestUserRegistration1.username()
        );
        this.usersRepository.saveAs(requestUserRegistration1, Password.of(hashPassword), invitationCode);
        this.invitationCodesRepository.saveAs(invitationCode);
    }
}
