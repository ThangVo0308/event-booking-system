package event_booking_system.demo.mappers;

import event_booking_system.demo.dtos.requests.events.EventRequest;
import event_booking_system.demo.dtos.responses.event.EventResponse;
import event_booking_system.demo.entities.Event;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    Event toEvent(EventRequest request);

    EventResponse toEventResponse(Event event);

    @AfterMapping
    default void customizeDto(Event entity, @MappingTarget EventResponse dto) {
        dto.setMUser(UserMapper.INSTANCE.toUserResponse(entity.getUser()));
    }
}
