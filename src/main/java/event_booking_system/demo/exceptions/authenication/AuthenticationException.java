package event_booking_system.demo.exceptions.authenication;

import event_booking_system.demo.exceptions.AppException;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class AuthenticationException extends AppException {

    public AuthenticationException(AuthenticationErrorCode authenticationErrorCode, HttpStatus httpStatus) {
        super(authenticationErrorCode.getMessage(), httpStatus);
        this.authenticationErrorCode = authenticationErrorCode;
    }

    private final AuthenticationErrorCode authenticationErrorCode;

}
