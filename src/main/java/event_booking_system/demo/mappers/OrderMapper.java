package event_booking_system.demo.mappers;


import event_booking_system.demo.dtos.requests.orders.OrderRequest;
import event_booking_system.demo.dtos.responses.order.OrderResponse;
import event_booking_system.demo.entities.Order;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    Order toOrder(OrderRequest request);

    OrderResponse toOrderResponse(Order order);

    @AfterMapping
    default void customizeDto(Order entity, @MappingTarget OrderResponse dto) {
        dto.setMUser(UserMapper.INSTANCE.toUserResponse(entity.getUser()));
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getOrderItems() != null) {
            dto.setMOrderItems(entity.getOrderItems().stream()
                    .map(OrderItemMapper.INSTANCE::toOrderItemResponse)
                    .collect(Collectors.toList()));
        } else {
            dto.setMOrderItems(Collections.emptyList());
        }
    }
}
