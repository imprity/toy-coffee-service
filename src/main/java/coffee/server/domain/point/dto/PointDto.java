package coffee.server.domain.point.dto;

import coffee.server.domain.point.entity.Point;
import java.math.BigDecimal;

public record PointDto(Long pointId, BigDecimal pointAmount) {

    public static PointDto of(Point point) {
        return new PointDto(point.getPointId(), point.getPointAmount());
    }
}
