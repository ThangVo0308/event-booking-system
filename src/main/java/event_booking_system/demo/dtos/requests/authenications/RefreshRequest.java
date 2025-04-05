package event_booking_system.demo.dtos.requests.authenications;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RefreshRequest (

    @NotNull(message = "null_field")
    @NotBlank(message = "blank_field")
    String refreshToken

) {
}