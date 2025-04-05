package event_booking_system.demo.mappers;

import event_booking_system.demo.dtos.requests.payments.PaymentRequest;
import event_booking_system.demo.dtos.responses.payment.PaymentResponse;
import event_booking_system.demo.entities.Payment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    Payment toPayment(PaymentRequest request);

    PaymentResponse toPaymentResponse(Payment payment);

    @AfterMapping
    default void customizeDto(Payment entity, @MappingTarget PaymentResponse dto) {
        dto.setMOrder(OrderMapper.INSTANCE.toOrderResponse(entity.getOrder()));
    }
}
