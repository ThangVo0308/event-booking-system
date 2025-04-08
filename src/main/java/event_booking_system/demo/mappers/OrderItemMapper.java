package event_booking_system.demo.mappers;

import event_booking_system.demo.dtos.responses.order.OrderItemResponse;
import event_booking_system.demo.entities.OrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    OrderItemResponse toOrderItemResponse(OrderItem entity);

    @AfterMapping
    default void customizeDto(OrderItem entity, @MappingTarget OrderItemResponse dto) {
        dto.setMTicket(
                TicketMapper.INSTANCE
                        .toTicketResponse(entity.getTicket()));
        dto.setCreatedAt(entity.getCreatedAt());
    }
}
