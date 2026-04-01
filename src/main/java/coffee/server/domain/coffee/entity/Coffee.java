package coffee.server.domain.coffee.entity;

import coffee.server.common.entity.BaseEntity;
import coffee.server.common.exception.ErrorCode;
import coffee.server.common.exception.ServiceException;
import coffee.server.common.exception.UserFacingServiceException;
import coffee.server.domain.coffee.dto.CoffeeDto;
import coffee.server.domain.coffee.enums.CoffeeStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
@Entity
@Table(name = "coffees")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coffee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coffeeId;

    private String coffeeName;

    private BigDecimal coffeePrice;

    private Long coffeeStock;

    @Enumerated(EnumType.STRING)
    private CoffeeStatus coffeeStatus;

    public static Coffee create(
            @NonNull String coffeeName,
            @NonNull BigDecimal coffeePrice,
            @NonNull Long coffeeStock,
            @NonNull CoffeeStatus coffeeStatus) {
        Coffee coffee = new Coffee();

        coffee.coffeeName = coffeeName;
        coffee.coffeePrice = coffeePrice;
        coffee.coffeeStock = coffeeStock;
        coffee.coffeeStatus = coffeeStatus;

        return coffee;
    }

    private void throwIfNotPositive(Long amount) {
        if (amount < 0) {
            throw new ServiceException(
                    ErrorCode.ERROR,
                    "tried to operate on coffee(id: %s)`s stock  with (%s) value. number should be >= 0"
                            .formatted(this.coffeeId, amount));
        }
    }

    public void decreaseStock(@NonNull Long amount) {
        throwIfNotPositive(amount);

        Long newStock = this.coffeeStock - amount;

        if (newStock < 0) {
            throw new UserFacingServiceException(
                    ErrorCode.COFFEE_INSUFFICIENT_STOCK,
                    HttpStatus.CONFLICT,
                    CoffeeDto.of(this),
                    "insufficient coffee stock. coffee stock(%s) < request amount(%s)"
                            .formatted(this.coffeeStock, amount));
        }

        this.coffeeStock = newStock;
    }

    public void increaseStock(@NonNull Long amount) {
        throwIfNotPositive(amount);

        this.coffeeStock += amount;
    }
}
