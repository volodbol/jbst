package jbst.iam.repositories.mongodb;

import jbst.iam.domain.db.InvitationCode;
import jbst.iam.domain.dto.requests.RequestNewInvitationCodeParams;
import jbst.iam.domain.dto.responses.ResponseInvitationCode;
import jbst.iam.domain.identifiers.InvitationId;
import jbst.iam.repositories.InvitationCodesRepository;
import jbst.iam.domain.mongodb.MongoDbInvitationCode;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.tuples.TuplePresence;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.stream.Collectors;

import static jbst.iam.domain.db.InvitationCode.INVITATION_CODES_UNUSED;
import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;
import static jbst.foundation.domain.tuples.TuplePresence.present;
import static java.util.Objects.nonNull;

public interface MongoInvitationCodesRepository extends MongoRepository<MongoDbInvitationCode, String>, InvitationCodesRepository {
    // ================================================================================================================
    // Any
    // ================================================================================================================
    default TuplePresence<InvitationCode> isPresent(InvitationId invitationId) {
        return this.findById(invitationId.value())
                .map(entity -> present(entity.invitationCode()))
                .orElseGet(TuplePresence::absent);
    }

    default List<ResponseInvitationCode> findResponseCodesByOwner(Username owner) {
        return this.findByOwner(owner).stream()
                .map(MongoDbInvitationCode::responseInvitationCode)
                .collect(Collectors.toList());
    }

    default InvitationCode findByValueAsAny(String value) {
        var invitationCode = this.findByValue(value);
        return nonNull(invitationCode) ? invitationCode.invitationCode() : null;
    }

    default List<ResponseInvitationCode> findUnused() {
        return this.findByInvitedIsNull(INVITATION_CODES_UNUSED).stream()
                .map(MongoDbInvitationCode::responseInvitationCode)
                .collect(Collectors.toList());
    }

    long countByOwner(Username username);

    default void delete(InvitationId invitationId) {
        this.deleteById(invitationId.value());
    }

    default InvitationId saveAs(InvitationCode invitationCode) {
        var entity = this.save(new MongoDbInvitationCode(invitationCode));
        return entity.invitationCodeId();
    }

    default InvitationId saveAs(Username owner, RequestNewInvitationCodeParams request) {
        var invitationCode = new MongoDbInvitationCode(
                owner,
                getSimpleGrantedAuthorities(request.authorities())
        );
        var entity = this.save(invitationCode);
        return entity.invitationCodeId();
    }

    // ================================================================================================================
    // Spring Data
    // ================================================================================================================
    List<MongoDbInvitationCode> findByOwner(Username username);
    List<MongoDbInvitationCode> findByInvitedIsNull();
    List<MongoDbInvitationCode> findByInvitedIsNull(Sort sort);
    List<MongoDbInvitationCode> findByInvitedIsNotNull();
    List<MongoDbInvitationCode> findByInvitedIsNotNull(Sort sort);
    MongoDbInvitationCode findByValue(String value);

    void deleteByInvitedIsNull();
    void deleteByInvitedIsNotNull();
}
