package event_booking_system.demo.dtos.requests.payments;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest (

        @NotNull(message = "null_field")
        @NotBlank(message = "blank_field")
        String orderId

)
{ }