package event_booking_system.demo.dtos.requests.orders;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record OrderItemRequest(
        @NotNull(message = "null_field") Date orderTime,
        @NotNull(message = "null_field") Double price,
        @NotNull(message = "null_field") @NotBlank(message = "blank_field") String orderId,
        @NotNull(message = "null_field") @NotBlank(message = "blank_field") String eventId
) {
}
