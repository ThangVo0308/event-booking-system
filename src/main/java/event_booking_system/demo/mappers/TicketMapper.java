package event_booking_system.demo.mappers;

import event_booking_system.demo.dtos.requests.authenications.SignUpRequest;
import event_booking_system.demo.dtos.requests.tickets.TicketRequest;
import event_booking_system.demo.dtos.responses.ticket.TicketResponse;
import event_booking_system.demo.entities.Ticket;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class);

    TicketMapper toTicket(TicketRequest request);

    TicketResponse toTicketResponse(Ticket ticket);

    @AfterMapping
    default void customizeDto(Ticket entity, @MappingTarget TicketResponse dto) {
        dto.setMEvent(EventMapper.INSTANCE.toEventResponse(entity.getEvent()));
    }
}
