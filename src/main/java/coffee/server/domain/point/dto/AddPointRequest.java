package coffee.server.domain.point.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record AddPointRequest(@NotNull BigDecimal pointAmount, @NotNull UUID idempotencyKey) {}
