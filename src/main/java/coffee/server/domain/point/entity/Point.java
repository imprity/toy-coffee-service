package coffee.server.domain.point.entity;

import coffee.server.common.entity.BaseEntity;
import coffee.server.common.exception.ErrorCode;
import coffee.server.common.exception.ServiceException;
import jakarta.persistence.Entity;
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
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    private BigDecimal pointAmount;

    public static Point create(@NonNull BigDecimal pointAmount) {
        Point point = new Point();
        point.pointAmount = pointAmount;

        return point;
    }

    public void updatePointAmount(@NonNull BigDecimal newPointAmount) {
        if (newPointAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException(
                    ErrorCode.ERROR,
                    "tried to set point(id %s)`s point amount with (%s) value. point amount should be >= 0"
                            .formatted(this.pointId, newPointAmount));
        }

        this.pointAmount = newPointAmount;
    }
}
