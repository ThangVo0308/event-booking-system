package event_booking_system.demo.dtos.requests.orders;

import event_booking_system.demo.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotNull(message = "null_field") @NotBlank(message = "blank_field") String userId,

        @NotNull(message = "null_field") @NotBlank(message = "blank_field") String ticketId,
        @NotNull(message = "null_field") Integer quantity,
        @NotNull(message = "null_field") Double price
) {
}
