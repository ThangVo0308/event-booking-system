package event_booking_system.demo.dtos.responses.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import event_booking_system.demo.dtos.responses.order.OrderResponse;
import event_booking_system.demo.dtos.responses.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventResponse {
    String id;
    String eventName;
    String description;
    String location;
    Date startTime;
    Date endTime;
    String userId;

    @JsonProperty(value = "user")
    UserResponse mUser;
}