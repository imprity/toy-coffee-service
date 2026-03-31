package coffee.server.domain.point.facade;

import coffee.server.domain.idempotencycache.service.IdempotencyCacheService;
import coffee.server.domain.point.dto.AddPointRequest;
import coffee.server.domain.point.dto.GetPointResponse;
import coffee.server.domain.point.dto.SetPointRequest;
import coffee.server.domain.point.service.PointService;
import coffee.server.domain.pointaudit.enums.PointAuditType;
import coffee.server.domain.pointaudit.service.PointAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.core.type.TypeReference;

@Component
@RequiredArgsConstructor
public class PointFacade {
    private final PointService pointService;
    private final IdempotencyCacheService idempotencyCacheService;
    private final PointAuditService pointAuditService;

    private final TransactionTemplate tx;

    @Transactional(readOnly = true)
    public GetPointResponse getPoint() {
        return pointService.getPoint();
    }

    public GetPointResponse setPoint(SetPointRequest req) {
        GetPointResponse cachedRes =
                idempotencyCacheService.getCache(req.idempotencyKey(), new TypeReference<GetPointResponse>() {});
        if (cachedRes != null) {
            return cachedRes;
        }

        GetPointResponse res = tx.execute((status) -> {
            GetPointResponse innerRes = pointService.setPoint(req.pointAmount());
            idempotencyCacheService.putCache(req.idempotencyKey(), innerRes);
            return innerRes;
        });

        pointAuditService.savePointAudit(res.pointId(), PointAuditType.POINT_SET, req.pointAmount(), null, null);

        return res;
    }

    public GetPointResponse addPoint(AddPointRequest req) {
        GetPointResponse cachedRes =
                idempotencyCacheService.getCache(req.idempotencyKey(), new TypeReference<GetPointResponse>() {});
        if (cachedRes != null) {
            return cachedRes;
        }

        GetPointResponse res = tx.execute((status) -> {
            GetPointResponse innerRes = pointService.addPoint(req.pointAmount());
            idempotencyCacheService.putCache(req.idempotencyKey(), innerRes);
            return innerRes;
        });

        pointAuditService.savePointAudit(res.pointId(), PointAuditType.POINT_ADD, req.pointAmount(), null, null);

        return res;
    }
}
