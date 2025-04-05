package event_booking_system.demo.dtos.requests.medias;

import event_booking_system.demo.enums.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MediaRequest(
        @NotNull(message = "null_field") @NotBlank(message = "blank_field") String eventId,
        @NotNull(message = "null_field") MediaType type
) {
}
