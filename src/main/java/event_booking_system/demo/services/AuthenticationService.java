package event_booking_system.demo.services;

import com.nimbusds.jose.JOSEException;
import event_booking_system.demo.entities.User;
import event_booking_system.demo.enums.SocialType;
import event_booking_system.demo.enums.VerificationType;
import jakarta.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.util.Map;

public interface AuthenticationService {
    // sign up/ sign in
    void signUp(User user, String confirmationPassword, boolean terms, boolean isOrganizer);

    User signIn(String email, String password);

    void signOut(String accessToken, String refreshToken) throws ParseException, JOSEException;

    void sendEmailVerification(String email, VerificationType verificationType);

    // token
    boolean introspect(String token)throws JOSEException, ParseException;

    String generateToken(User user, boolean isRefresh);

    User refreshToken(String refreshToken, HttpServletRequest request) throws JOSEException, ParseException;

    void verifyEmail(User user, String code, String token);

    // social
    String generateSocialLogin(SocialType type);

    Map<String, Object> fetchSocialUser(String code, SocialType type) throws Exception;

}
