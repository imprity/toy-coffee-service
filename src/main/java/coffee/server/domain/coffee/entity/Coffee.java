package coffee.server.domain.coffee.entity;

import coffee.server.common.entity.BaseEntity;
import coffee.server.common.exception.ErrorCode;
import coffee.server.common.exception.ServiceException;
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

        throwIfCoffeeStockNotPositive(
                coffeeStock, "tried to create coffee with negative stock(%s)".formatted(coffeeStock));

        Coffee coffee = new Coffee();

        coffee.coffeeName = coffeeName;
        coffee.coffeePrice = coffeePrice;
        coffee.coffeeStock = coffeeStock;
        coffee.coffeeStatus = coffeeStatus;

        return coffee;
    }

    public void updateCoffeeStock(@NonNull Long newCoffeeStock) {
        throwIfCoffeeStockNotPositive(
                newCoffeeStock, "tried to set coffee stock with negative stock(%s)".formatted(coffeeStock));

        this.coffeeStock = newCoffeeStock;
    }

    private static void throwIfCoffeeStockNotPositive(Long coffeeStock, String message) {
        if (coffeeStock < 0) {
            throw new ServiceException(ErrorCode.COFFEE_NEGATIVE_COFFEE_STOCK, message);
        }
    }
}
