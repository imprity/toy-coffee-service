package coffee.server.domain.coffeeorder.entity;

import coffee.server.common.entity.BaseEntity;
import coffee.server.domain.coffee.dto.CoffeeDto;
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
import org.jspecify.annotations.Nullable;

@Getter
@Entity
@Table(name = "coffee_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("NullAway.Init")
public class CoffeeOrder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coffeeOrderId;

    private @Nullable Long coffeeId;

    private String coffeeSnapshotName;

    private BigDecimal coffeeSnapshotPrice;

    private Long coffeeOrderAmount;

    private String customerId;

    public static CoffeeOrder create(Coffee coffee, Long coffeeOrderAmount, String customerId) {
        CoffeeOrder coffeeOrder = new CoffeeOrder();

        coffeeOrder.coffeeId = coffee.getCoffeeId();
        coffeeOrder.coffeeSnapshotName = coffee.getCoffeeName();
        coffeeOrder.coffeeSnapshotPrice = coffee.getCoffeePrice();
        coffeeOrder.coffeeOrderAmount = coffeeOrderAmount;
        coffeeOrder.customerId = customerId;

        return coffeeOrder;
    }

    public static CoffeeOrder create(CoffeeDto coffeeDto, Long coffeeOrderAmount, String customerId) {
        CoffeeOrder coffeeOrder = new CoffeeOrder();

        coffeeOrder.coffeeId = coffeeDto.coffeeId();
        coffeeOrder.coffeeSnapshotName = coffeeDto.coffeeName();
        coffeeOrder.coffeeSnapshotPrice = coffeeDto.coffeePrice();
        coffeeOrder.coffeeOrderAmount = coffeeOrderAmount;
        coffeeOrder.customerId = customerId;

        return coffeeOrder;
    }
}
