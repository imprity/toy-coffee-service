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
import lombok.NonNull;

@Getter
@Entity
@Table(name = "point_audits")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointAudit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointAuditId;

    private Long pointId;

    @Enumerated(EnumType.STRING)
    private PointAuditType pointAuditType;

    private BigDecimal pointAuditAmount;

    private Long coffeeOrderId;

    private String customerId;

    public static PointAudit create(
            @NonNull Long pointId,
            @NonNull PointAuditType auditType,
            @NonNull BigDecimal auditAmount,
            Long coffeeOrderId,
            String customerId) {
        PointAudit pointAudit = new PointAudit();

        pointAudit.pointId = pointId;
        pointAudit.pointAuditType = auditType;
        pointAudit.pointAuditAmount = auditAmount;
        pointAudit.coffeeOrderId = coffeeOrderId;
        pointAudit.customerId = customerId;

        return pointAudit;
    }
}
