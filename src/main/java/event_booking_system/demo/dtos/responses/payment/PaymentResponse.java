package event_booking_system.demo.dtos.responses.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import event_booking_system.demo.dtos.responses.order.OrderResponse;
import event_booking_system.demo.enums.PaymentMethod;
import event_booking_system.demo.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {

    String id;

    PaymentMethod method;

    Double price;

    PaymentStatus status;

    @JsonProperty(value = "order")
    OrderResponse mOrder;
}
