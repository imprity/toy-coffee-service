package coffee.server.domain.pointaudit.service;

import coffee.server.domain.pointaudit.dto.PointAuditDto;
import coffee.server.domain.pointaudit.entity.PointAudit;
import coffee.server.domain.pointaudit.enums.PointAuditType;
import coffee.server.domain.pointaudit.repository.PointAuditRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointAuditService {
    private final PointAuditRepository pointAuditRepository;
    private final PlatformTransactionManager txManager;

    @Nullable
    public PointAuditDto savePointAudit(
            Long pointId,
            PointAuditType auditType,
            BigDecimal auditAmount,
            @Nullable Long coffeeOrderId,
            @Nullable String customerId) {

        // 선언적으로 @Transactional을 쓰는 대신에 PlatformTransactionManager를 쓰면 테스팅이 어려워집니다...
        //
        // 하지만 @Transactional을 쓸 경우 Exception이 일어 났을 때 catch를 하더라도 rollback을 하도록 marking이 됩니다.
        // 이때 @Transactional이 달린 method가 Exception을 던지지 않고 catch를 했을 경우
        // Spring은 `어, 너 rollback 하라고 찍혀 있는데 왜 Exception 안던졌어?` 하면서
        //
        // 지맘대로 UnexpectedRollbackException을 던집니다.
        //
        // 하지만 Audit을 저장하는데 실패하든 성공하든 business logic에는 간섭을 하면 안되기 때문에 이렇게 했습니다.
        TransactionTemplate tx = new TransactionTemplate(txManager);
        tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        PointAuditDto dto = null;

        try {
            dto = tx.execute((status) -> {
                PointAudit audit = PointAudit.create(pointId, auditType, auditAmount, coffeeOrderId, customerId);

                audit = pointAuditRepository.saveAndFlush(audit);
                return PointAuditDto.of(audit);
            });
        } catch (Exception err) {
            log.error(
                    "failed to save PointAudit " + "pointId: {}, "
                            + "auditType: {}, "
                            + "auditAmount: {}, "
                            + "coffeeOrderId: {}, "
                            + "customerId: {}",
                    pointId,
                    auditType,
                    auditAmount,
                    coffeeOrderId,
                    customerId,
                    err);
        }

        return dto;
    }
}
