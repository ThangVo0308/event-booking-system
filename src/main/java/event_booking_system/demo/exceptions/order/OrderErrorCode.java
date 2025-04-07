package event_booking_system.demo.exceptions.order;

import lombok.Getter;

@Getter
public enum OrderErrorCode {
    BOOKING_CHECK_PENDING("booking/check-pending", "booking_check_pending"),

    USER_BANNED("booking/user-banned", "user_is_banned"),

    BOOKING_FAILED("booking/failed", "booking_failed"),

    CANCEL_FAILED("booking/cancel-failed", "cancel_failed"),
    ;

    OrderErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private final String code;
    private final String message;
}
