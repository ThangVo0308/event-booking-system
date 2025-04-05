package event_booking_system.demo.dtos.responses.checkin;

import com.fasterxml.jackson.annotation.JsonProperty;
import event_booking_system.demo.dtos.responses.event.EventResponse;
import event_booking_system.demo.dtos.responses.order.OrderResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckinResponse {
    String id;
    String orderId;
    Date checkinTime;
    String qrCode;

    @JsonProperty(value = "order")
    OrderResponse mOrder;
}
