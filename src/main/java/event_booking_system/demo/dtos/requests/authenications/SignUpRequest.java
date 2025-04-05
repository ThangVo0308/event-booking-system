package event_booking_system.demo.dtos.requests.authenications;

import event_booking_system.demo.enums.Gender;
import event_booking_system.demo.validates.Adult;
import event_booking_system.demo.validates.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SignUpRequest (

        @NotNull(message = "null_field")
        @NotBlank(message = "blank_field")
        String username,

        @NotNull(message = "null_field")
        @NotBlank(message = "blank_field")
        @Email(message = "invalid_email")
        String email,

        @NotNull(message = "null_field")
        @NotBlank(message = "blank_field")
        @Size(min = 6, max = 20, message = "size_field")
        String password,

        @NotNull(message = "null_field")
        @NotBlank(message = "blank_field")
        @Size(min = 6, max = 20, message = "size_field")
        String passwordConfirmation,

        @NotNull(message = "null_field")
        @NotBlank(message = "blank_field")
        @Phone
        String mobileNumber,

        @Adult
        LocalDate birthdate,

        Gender gender,

        boolean isOrganizer,

        boolean acceptTerms

) {

}