package coffee.server.domain.point.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GetPointRequest(
        @NotBlank(message = "customerId should not be blank.")
                @Size(max = 64, message = "customerId shuould be less than 64 characters.")
                String customerId) {}
