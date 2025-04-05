package event_booking_system.demo.dtos.requests.authenications;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VerifyEmailByCodeRequest(
        @NotNull(message = "null_field")
        @NotBlank(message = "blank_field")
        @Email(message = "invalid_email")
        String email,

        @NotNull(message = "null_field")
        @NotBlank(message = "blank_field")
        @Size(min = 6, max = 6, message = "code_invalid")
        String code
) {
}
