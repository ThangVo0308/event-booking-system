package event_booking_system.demo.exceptions.payment;

import event_booking_system.demo.exceptions.AppException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PaymentException extends AppException {
    private final PaymentErrorCode paymentErrorCode;

    public PaymentException(PaymentErrorCode paymentErrorCode, HttpStatus httpStatus) {
        super(paymentErrorCode.getMessage(), httpStatus);
        this.paymentErrorCode = paymentErrorCode;
    }
}
