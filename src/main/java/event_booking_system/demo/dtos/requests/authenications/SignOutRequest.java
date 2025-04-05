package event_booking_system.demo.dtos.requests.authenications;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignOutRequest (

    @NotNull(message = "null_field")
    @NotBlank(message = "blank_field")
    String accessToken,

    @NotNull(message = "null_field")
    @NotBlank(message = "blank_field")
    String refreshToken

) {
}