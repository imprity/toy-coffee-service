package coffee.server.domain.point.facade;

import coffee.server.domain.idempotencycache.service.IdempotencyCacheService;
import coffee.server.domain.point.dto.AddPointRequest;
import coffee.server.domain.point.dto.GetPointResponse;
import coffee.server.domain.point.dto.SetPointRequest;
import coffee.server.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;

@Component
@RequiredArgsConstructor
public class PointFacade {
    private final PointService pointService;
    private final IdempotencyCacheService idempotencyCacheService;

    @Transactional(readOnly = true)
    public GetPointResponse getPoint() {
        return pointService.getPoint();
    }

    @Transactional()
    public GetPointResponse setPoint(SetPointRequest req) {
        GetPointResponse cachedRes =
                idempotencyCacheService.getCache(req.idempotencyKey(), new TypeReference<GetPointResponse>() {});
        if (cachedRes != null) {
            return cachedRes;
        }

        GetPointResponse res = pointService.setPoint(req.pointAmount());
        idempotencyCacheService.putCache(req.idempotencyKey(), res);

        return res;
    }

    @Transactional()
    public GetPointResponse addPoint(AddPointRequest req) {
        GetPointResponse cachedRes =
                idempotencyCacheService.getCache(req.idempotencyKey(), new TypeReference<GetPointResponse>() {});
        if (cachedRes != null) {
            return cachedRes;
        }

        GetPointResponse res = pointService.addPoint(req.pointAmount());
        idempotencyCacheService.putCache(req.idempotencyKey(), res);

        return res;
    }
}
