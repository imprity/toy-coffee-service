package coffee.server.domain.point.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record SetPointRequest(
        @NotNull @Min(0) BigDecimal pointAmount,
        @NotNull UUID idempotencyKey,
        @NotBlank(message = "customerId should not be blank.")
                @Size(max = 64, message = "customerId shuould be less than 64 characters.")
                String customerId) {}
