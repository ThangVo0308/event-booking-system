package event_booking_system.demo.mappers;

import event_booking_system.demo.dtos.requests.medias.MediaRequest;
import event_booking_system.demo.dtos.requests.payments.PaymentRequest;
import event_booking_system.demo.dtos.responses.event.EventResponse;
import event_booking_system.demo.dtos.responses.media.MediaResponse;
import event_booking_system.demo.dtos.responses.payment.PaymentResponse;
import event_booking_system.demo.entities.Event;
import event_booking_system.demo.entities.Media;
import event_booking_system.demo.entities.Payment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MediaMapper {
    MediaMapper INSTANCE = Mappers.getMapper(MediaMapper.class);

    Media toMedia(MediaRequest request);

    MediaResponse toMediaResponse(Media media);

    @AfterMapping
    default void customizeDto(Media entity, @MappingTarget MediaResponse dto) {
        dto.setMEvent(EventMapper.INSTANCE.toEventResponse(entity.getEvent()));
    }
}
