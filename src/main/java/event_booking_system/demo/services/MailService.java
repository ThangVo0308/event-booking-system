package event_booking_system.demo.services;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public interface MailService {
    void sendEmailVerificationWithToken(String to, String token) throws MessagingException, UnsupportedEncodingException;
    void sendEmailVerificationWithCode(String to, String code) throws MessagingException, UnsupportedEncodingException;
    void sendEmailToResetPassword(String to, String code) throws MessagingException, UnsupportedEncodingException;
}
