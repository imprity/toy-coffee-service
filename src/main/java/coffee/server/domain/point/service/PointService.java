package coffee.server.domain.point.service;

import coffee.server.common.config.AppConfig;
import coffee.server.domain.point.dto.GetPointResponse;
import coffee.server.domain.point.entity.Point;
import coffee.server.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final AppConfig appConfig;

    @Transactional(readOnly = true)
    public GetPointResponse getPoint() {
        Long pointId = appConfig.getPointIdInDatabase();

        Point point = pointRepository
                .findById(pointId)
                .orElseThrow(() -> new RuntimeException("poind %s not found".formatted(pointId)));

        return GetPointResponse.of(point);
    }
}
