package event_booking_system.demo.mappers;

import event_booking_system.demo.dtos.requests.authenications.SignUpRequest;

import event_booking_system.demo.dtos.requests.checkins.CheckinRequest;
import event_booking_system.demo.dtos.responses.checkin.CheckinResponse;
import event_booking_system.demo.dtos.responses.ticket.TicketResponse;
import event_booking_system.demo.entities.Checkin;
import event_booking_system.demo.entities.Ticket;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CheckinMapper {
    CheckinMapper INSTANCE = Mappers.getMapper(CheckinMapper.class);

    Checkin toCheckin(CheckinRequest request);

    CheckinResponse toCheckinResponse(Checkin checkin);

    @AfterMapping
    default void customizeDto(Checkin entity, @MappingTarget CheckinResponse dto) {
        dto.setMOrder(OrderMapper.INSTANCE.toOrderResponse(entity.getOrder()));
    }
}
