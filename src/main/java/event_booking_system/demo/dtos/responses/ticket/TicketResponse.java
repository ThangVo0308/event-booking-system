package event_booking_system.demo.dtos.responses.ticket;

import com.fasterxml.jackson.annotation.JsonProperty;
import event_booking_system.demo.dtos.responses.event.EventResponse;
import event_booking_system.demo.enums.TicketType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketResponse {
    String id;
    String eventId;
    TicketType type;
    Double price;
    Integer totalQuantity;
    Integer availableQuantity;

    @JsonProperty(value = "event")
    EventResponse mEvent;
}
