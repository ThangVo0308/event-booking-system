package event_booking_system.demo.dtos.responses.authentication;

public record SendEmailForgotPasswordResponse(

    String message,

    int retryAfter

) {

}