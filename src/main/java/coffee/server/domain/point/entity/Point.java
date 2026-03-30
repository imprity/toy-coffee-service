package coffee.server.domain.point.entity;

import coffee.server.common.entity.BaseEntity;
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

    private static BigDecimal checkPositive(BigDecimal number, String message) {
        if (number.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException(message);
        }
        return number;
    }

    public Point setPointAmount(@NonNull BigDecimal newPointAmount) {
        checkPositive(newPointAmount, "can't set point amount below zero");

        this.pointAmount = newPointAmount;

        return this;
    }

    public Point addPointAmount(@NonNull BigDecimal toAdd) {
        BigDecimal newPointAmount = this.pointAmount.add(toAdd);
        checkPositive(newPointAmount, "(%s) + (%s) < 0".formatted(this.pointAmount, toAdd));
        this.pointAmount = newPointAmount;

        return this;
    }

    public Point subPointAmount(@NonNull BigDecimal toSub) {
        BigDecimal newPointAmount = this.pointAmount.subtract(toSub);
        checkPositive(newPointAmount, "(%s) - (%s) < 0".formatted(this.pointAmount, toSub));
        this.pointAmount = newPointAmount;

        return this;
    }
}
