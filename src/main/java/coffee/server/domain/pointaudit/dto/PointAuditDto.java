package coffee.server.domain.pointaudit.dto;

import coffee.server.domain.pointaudit.entity.PointAudit;
import coffee.server.domain.pointaudit.enums.PointAuditType;
import java.math.BigDecimal;

public record PointAuditDto(
        Long pointAuditId,
        Long pointId,
        PointAuditType pointAuditType,
        BigDecimal pointAuditAmount,
        Long coffeeOrderId,
        String customerId) {
    public static PointAuditDto of(PointAudit audit) {
        return new PointAuditDto(
                audit.getPointAuditId(),
                audit.getPointId(),
                audit.getPointAuditType(),
                audit.getPointAuditAmount(),
                audit.getCoffeeOrderId(),
                audit.getCustomerId());
    }
}
