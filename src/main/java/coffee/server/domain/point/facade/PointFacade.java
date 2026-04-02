package coffee.server.domain.point.facade;

import coffee.server.common.exception.ErrorCode;
import coffee.server.common.exception.ServiceException;
import coffee.server.domain.idempotencycache.exception.DuplicateCacheKeyException;
import coffee.server.domain.idempotencycache.service.IdempotencyCacheService;
import coffee.server.domain.point.dto.AddPointRequest;
import coffee.server.domain.point.dto.PointDto;
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
    public PointDto getPoint() {
        return pointService.getPoint();
    }

    public PointDto setPoint(SetPointRequest req) {
        PointDto cachedRes = idempotencyCacheService.getCache(req.idempotencyKey(), new TypeReference<PointDto>() {});
        if (cachedRes != null) {
            return cachedRes;
        }

        try {
            PointDto res = tx.execute((status) -> {
                PointDto innerRes = pointService.setPoint(req.pointAmount());
                idempotencyCacheService.putCache(req.idempotencyKey(), innerRes);
                return innerRes;
            });

            pointAuditService.savePointAudit(res.pointId(), PointAuditType.POINT_SET, req.pointAmount(), null, null);

            return res;
        } catch (DuplicateCacheKeyException e) {
            cachedRes = idempotencyCacheService.getCache(req.idempotencyKey(), new TypeReference<PointDto>() {});
            if (cachedRes != null) {
                return cachedRes;
            } else {
                throw new ServiceException(
                        ErrorCode.ERROR,
                        "Could not find idempotencyCacheKey (%s) even though it was supposed to be in idempotency_caches table."
                                .formatted(req.idempotencyKey()));
            }
        }
    }

    public PointDto addPoint(AddPointRequest req) {
        PointDto cachedRes = idempotencyCacheService.getCache(req.idempotencyKey(), new TypeReference<PointDto>() {});
        if (cachedRes != null) {
            return cachedRes;
        }

        try {
            PointDto res = tx.execute((status) -> {
                PointDto innerRes = pointService.addPoint(req.pointAmount());
                idempotencyCacheService.putCache(req.idempotencyKey(), innerRes);
                return innerRes;
            });

            pointAuditService.savePointAudit(res.pointId(), PointAuditType.POINT_ADD, req.pointAmount(), null, null);
            return res;
        } catch (DuplicateCacheKeyException e) {
            cachedRes = idempotencyCacheService.getCache(req.idempotencyKey(), new TypeReference<PointDto>() {});
            if (cachedRes != null) {
                return cachedRes;
            } else {
                throw new ServiceException(
                        ErrorCode.ERROR,
                        "Could not find idempotencyCacheKey (%s) even though it was supposed to be in idempotency_caches table."
                                .formatted(req.idempotencyKey()));
            }
        }
    }
}
