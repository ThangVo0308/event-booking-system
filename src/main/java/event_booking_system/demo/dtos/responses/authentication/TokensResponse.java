package event_booking_system.demo.dtos.responses.authentication;

public record TokensResponse(

    String accessToken,
    String refreshToken

) {
}
