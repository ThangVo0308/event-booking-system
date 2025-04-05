package event_booking_system.demo.dtos.requests.events;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record EventRequest(
        @NotNull(message = "null_field") @NotBlank(message = "blank_field") String eventName,
        @NotNull(message = "null_field") @NotBlank(message = "blank_field") String description,
        @NotNull(message = "null_field") @NotBlank(message = "blank_field") String location,
        @NotNull(message = "null_field") Date startTime,
        @NotNull(message = "null_field") Date endTime,
        @NotNull(message = "null_field") @NotBlank(message = "blank_field") String userId
) {
}