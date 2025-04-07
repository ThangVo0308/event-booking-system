package event_booking_system.demo.dtos.responses.vnpay;

public record VNPayResponse(
        String code,
        String message,
        String paymentUrl
) {
}