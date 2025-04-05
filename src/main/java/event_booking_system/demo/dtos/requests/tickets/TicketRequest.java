package event_booking_system.demo.dtos.requests.tickets;

import event_booking_system.demo.enums.TicketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketRequest(
        @NotNull(message = "null_field") @NotBlank(message = "blank_field") String eventId,
        @NotNull(message = "null_field") TicketType type,
        @NotNull(message = "null_field") Double price,
        @NotNull(message = "null_field") Integer totalQuantity,
        @NotNull(message = "null_field") Integer availableQuantity
) {
}
