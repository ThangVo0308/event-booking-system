package event_booking_system.demo.exceptions.order;

import event_booking_system.demo.exceptions.AppException;
import event_booking_system.demo.exceptions.CommonErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class OrderException extends AppException {
    private final OrderErrorCode orderErrorCode;
    public OrderException(OrderErrorCode orderErrorCode, HttpStatus status) {
        super(orderErrorCode.getMessage(), status);
        this.orderErrorCode = orderErrorCode;
    }
}
