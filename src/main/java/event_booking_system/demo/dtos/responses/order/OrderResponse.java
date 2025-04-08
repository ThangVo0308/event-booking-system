package event_booking_system.demo.dtos.responses.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import event_booking_system.demo.dtos.responses.ticket.TicketResponse;
import event_booking_system.demo.dtos.responses.user.UserResponse;
import event_booking_system.demo.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;

    OrderStatus status;

    Date createdAt;

    @JsonProperty(value = "user")
    UserResponse mUser;

    @JsonProperty(value = "order_items")
    List<OrderItemResponse> mOrderItems;
}
