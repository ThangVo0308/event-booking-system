package event_booking_system.demo.dtos.requests.authenications;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest (

        @NotNull(message = "null_field")
        @NotBlank(message = "blank_field")
        @Email(message = "invalid_email")
        String email,

        @NotNull(message = "code_invalid")
        @NotBlank(message = "code_invalid")
        @Size(min = 6, max = 6, message = "code_invalid")
        String code

) {
}
