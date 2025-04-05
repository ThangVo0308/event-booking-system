package event_booking_system.demo.dtos.responses.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import event_booking_system.demo.dtos.responses.event.EventResponse;
import event_booking_system.demo.enums.MediaType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaResponse {
    String id;
    String eventId;
    MediaType type;

    @JsonProperty(value = "event")
    EventResponse mEvent;
}
