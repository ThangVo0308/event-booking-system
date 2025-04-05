package event_booking_system.demo.dtos.requests.roles;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoleRequest(
        @NotNull(message = "null_field") @NotBlank(message = "blank_field") String name
) {
}