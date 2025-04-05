package event_booking_system.demo.dtos.requests.checkins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record CheckinRequest(
        @NotNull(message = "null_field") @NotBlank(message = "blank_field") String orderId,
        @NotNull(message = "null_field") Date checkinTime,
        @NotNull(message = "null_field") @NotBlank(message = "blank_field") String qrCode
) {
}
