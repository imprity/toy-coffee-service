package coffee.server.domain.point.dto;

import coffee.server.domain.point.entity.Point;
import java.math.BigDecimal;

public record GetPointResponse(Long pointId, BigDecimal pointAmount) {

    public static GetPointResponse of(Point point) {
        return new GetPointResponse(point.getPointId(), point.getPointAmount());
    }
}
