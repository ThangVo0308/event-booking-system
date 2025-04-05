package event_booking_system.demo.dtos.requests.authenications;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendEmailForgotPasswordRequest(

    @NotNull(message = "null_field")
    @NotBlank(message = "blank_field")
    @Email(message = "invalid_email")
    String email

) {

}