package event_booking_system.demo.exceptions;

import lombok.Getter;

@Getter
public enum CommonErrorCode {
    USER_BANNED("USER-BANNED", "user_banned"),
    OBJECT_NOT_FOUND("OBJECT-NOT-FOUND", "object_not_found"),
    ROLE_NOT_FOUND("ROLE-NOT-FOUND", "role_not_found"),
    EVENT_NOT_FOUND("EVENT-NOT-FOUND", "event_not_found"),
    TICKET_NOT_FOUND("TICKET-NOT-FOUND", "ticket_not_found"),
    ORDER_NOT_FOUND("ORDER-NOT-FOUND", "order_not_found"),
    PAYMENT_NOT_FOUND("PAYMENT-NOT-FOUND", "payment_not_found"),
    CHECKIN_NOT_FOUND("CHECKIN-NOT-FOUND", "checkin_not_found"),
    QR_CODE_ALREADY_USED("QRCODE-ALREADY-USED", "qrcode_already_used"),
    INSUFFICIENT_TICKETS("INSUFFICIENT-TICKETS", "insufficient_tickets"),
    ;

    CommonErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private final String code;
    private final String message;

}

