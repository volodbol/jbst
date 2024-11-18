package jbst.iam.domain.postgres.superclasses;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech1.framework.foundation.domain.base.Username;
import tech1.framework.foundation.domain.converters.columns.PostgresUsernameConverter;

import static tech1.framework.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class PostgresDbAbstractPersistable2 extends PostgresDbAbstractPersistable0 {

    @Convert(converter = PostgresUsernameConverter.class)
    @Column(name = "created_by", nullable = false)
    protected Username createdBy;

    @Column(name = "created_at", nullable = false)
    protected long createdAt;

    protected PostgresDbAbstractPersistable2(Username createdBy) {
        this.createdBy = createdBy;
        this.createdAt = getCurrentTimestamp();
    }
}
