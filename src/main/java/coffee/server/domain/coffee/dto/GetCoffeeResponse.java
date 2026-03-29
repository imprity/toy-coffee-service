package coffee.server.domain.coffee.dto;

import coffee.server.domain.coffee.entity.Coffee;
import coffee.server.domain.coffee.enums.CoffeeStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record GetCoffeeResponse(
        Long coffeeId,
        String coffeeName,
        BigDecimal coffeePrice,
        Long coffeeStock,
        CoffeeStatus coffeeStatus,
        Instant createdAt,
        Instant modifiedAt) {
    public static GetCoffeeResponse of(Coffee coffee) {
        return new GetCoffeeResponse(
                coffee.getCoffeeId(),
                coffee.getCoffeeName(),
                coffee.getCoffeePrice(),
                coffee.getCoffeeStock(),
                coffee.getCoffeeStatus(),
                coffee.getCreatedAt(),
                coffee.getModifiedAt());
    }
}
