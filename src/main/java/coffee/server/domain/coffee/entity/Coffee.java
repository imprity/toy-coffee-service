package coffee.server.domain.coffee.entity;

import coffee.server.common.entity.BaseEntity;
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
        Coffee coffee = new Coffee();

        coffee.coffeeName = coffeeName;
        coffee.coffeePrice = coffeePrice;
        coffee.coffeeStock = coffeeStock;
        coffee.coffeeStatus = coffeeStatus;

        return coffee;
    }
}
