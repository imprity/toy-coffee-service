package coffee.server.domain.pointaudit.entity;

import coffee.server.common.entity.BaseEntity;
import coffee.server.domain.pointaudit.enums.PointAuditType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

@Getter
@Entity
@Table(name = "point_audits")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("NullAway.Init")
public class PointAudit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointAuditId;

    private Long pointId;

    @Enumerated(EnumType.STRING)
    private PointAuditType pointAuditType;

    private BigDecimal pointAuditAmount;

    private @Nullable Long coffeeOrderId;

    private @Nullable String customerId;

    public static PointAudit create(
            Long pointId,
            PointAuditType auditType,
            BigDecimal auditAmount,
            @Nullable Long coffeeOrderId,
            @Nullable String customerId) {
        PointAudit pointAudit = new PointAudit();

        pointAudit.pointId = pointId;
        pointAudit.pointAuditType = auditType;
        pointAudit.pointAuditAmount = auditAmount;
        pointAudit.coffeeOrderId = coffeeOrderId;
        pointAudit.customerId = customerId;

        return pointAudit;
    }
}
