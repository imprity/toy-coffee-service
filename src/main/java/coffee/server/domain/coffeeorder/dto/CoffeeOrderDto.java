package coffee.server.domain.coffeeorder.dto;

import coffee.server.domain.coffeeorder.entity.CoffeeOrder;
import java.math.BigDecimal;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

public record CoffeeOrderDto(
        Long coffeeOrderId,
        @Nullable Long coffeeId,
        String coffeeSnapshotName,
        BigDecimal coffeeSnapshotPrice,
        Long coffeeOrderAmount,
        String customerId,
        Instant createdAt,
        Instant modifiedAt) {
    public static CoffeeOrderDto of(CoffeeOrder order) {
        return new CoffeeOrderDto(
                order.getCoffeeOrderId(),
                order.getCoffeeId(),
                order.getCoffeeSnapshotName(),
                order.getCoffeeSnapshotPrice(),
                order.getCoffeeOrderAmount(),
                order.getCustomerId(),
                order.getCreatedAt(),
                order.getModifiedAt());
    }
}
