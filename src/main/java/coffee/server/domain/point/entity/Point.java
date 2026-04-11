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
import org.jspecify.annotations.Nullable;

@Getter
@Entity
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("NullAway.Init")
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    private BigDecimal pointAmount;

    private @Nullable String customerId;

    public static Point create(BigDecimal pointAmount, String customerId) {
        Point point = new Point();
        point.pointAmount = pointAmount;
        point.customerId = customerId;

        return point;
    }

    public void updatePointAmount(BigDecimal newPointAmount) {
        if (newPointAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException(
                    ErrorCode.ERROR,
                    "Tried to set point(id %s)`s point amount with (%s) value. Point amount should be >= 0."
                            .formatted(this.pointId, newPointAmount));
        }

        this.pointAmount = newPointAmount;
    }
}
