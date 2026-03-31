package coffee.server.domain.coffeeorder.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CoffeeOrderRequest(
        @NotNull UUID idempotencyKey,
        @NotNull Long coffeeId,
        @NotNull @Min(1) Long coffeeOrderAmount,
        @NotNull String customerId) {}
