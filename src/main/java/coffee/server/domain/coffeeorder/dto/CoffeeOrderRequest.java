package coffee.server.domain.coffeeorder.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CoffeeOrderRequest(
        @NotNull UUID idempotencyKey,
        @NotNull Long coffeeId,
        @NotNull @Min(1) Long coffeeOrderAmount,
        @NotBlank(message = "customerId should not be blank.")
                @Size(max = 64, message = "customerId shuould be less than 64 characters.")
                String customerId) {}
