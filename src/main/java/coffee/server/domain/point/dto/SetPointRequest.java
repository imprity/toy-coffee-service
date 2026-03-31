package coffee.server.domain.point.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record SetPointRequest(@NotNull @Min(0) BigDecimal pointAmount, @NotNull UUID idempotencyKey) {}
