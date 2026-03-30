package coffee.server.domain.point.service;

import coffee.server.common.config.AppConfig;
import coffee.server.domain.point.dto.GetPointResponse;
import coffee.server.domain.point.entity.Point;
import coffee.server.domain.point.repository.PointRepository;
import java.math.BigDecimal;
import lombok.NonNull;
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
                .orElseThrow(() -> new RuntimeException("point %s not found".formatted(pointId)));

        return GetPointResponse.of(point);
    }

    @Transactional()
    public GetPointResponse setPoint(@NonNull BigDecimal amount) {
        Long pointId = appConfig.getPointIdInDatabase();

        Point point = pointRepository
                .findById(pointId)
                .orElseThrow(() -> new RuntimeException("point %s not found".formatted(pointId)));

        point.setPointAmount(amount);

        point = pointRepository.saveAndFlush(point);

        return GetPointResponse.of(point);
    }

    @Transactional()
    public GetPointResponse addPoint(@NonNull BigDecimal toAdd) {
        Long pointId = appConfig.getPointIdInDatabase();

        Point point = pointRepository
                .findById(pointId)
                .orElseThrow(() -> new RuntimeException("point %s not found".formatted(pointId)));

        point.addPointAmount(toAdd);

        point = pointRepository.saveAndFlush(point);

        return GetPointResponse.of(point);
    }

    @Transactional()
    public GetPointResponse subPoint(@NonNull BigDecimal toSub) {
        Long pointId = appConfig.getPointIdInDatabase();

        Point point = pointRepository
                .findById(pointId)
                .orElseThrow(() -> new RuntimeException("point %s not found".formatted(pointId)));

        point.subPointAmount(toSub);

        point = pointRepository.saveAndFlush(point);

        return GetPointResponse.of(point);
    }
}
