package coffee.server.domain.coffeeorder.entity;

import coffee.server.common.entity.BaseEntity;
import coffee.server.domain.coffee.entity.Coffee;
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
@Table(name = "coffee_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoffeeOrder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coffeeOrderId;

    private Long coffeeId;

    private String coffeeSnapshotName;

    private BigDecimal coffeeSnapshotPrice;

    private Long coffeeOrderAmount;

    private String customerId;

    public static CoffeeOrder create(
            @NonNull Coffee coffee, @NonNull Long coffeeOrderAmount, @NonNull String customerId) {
        CoffeeOrder coffeeOrder = new CoffeeOrder();

        coffeeOrder.coffeeId = coffee.getCoffeeId();
        coffeeOrder.coffeeSnapshotName = coffee.getCoffeeName();
        coffeeOrder.coffeeSnapshotPrice = coffee.getCoffeePrice();
        coffeeOrder.coffeeOrderAmount = coffeeOrderAmount;
        coffeeOrder.customerId = customerId;

        return coffeeOrder;
    }
}
