package event_booking_system.demo.dtos.responses.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import event_booking_system.demo.dtos.responses.event.EventResponse;
import event_booking_system.demo.dtos.responses.ticket.TicketResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    String id;

    Integer quantity;
    Date orderTime;

    Date createdAt;

    Double price;

    @JsonProperty(value = "createdBy")
    String createdBy;

    @JsonProperty(value = "ticket")
    TicketResponse mTicket;
}
