package event_booking_system.demo.services.impls;

import event_booking_system.demo.services.MailService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailServiceImpl implements MailService {
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    @NonFinal
    String from;

    @Value("${server.port}")
    @NonFinal
    String port;

    @Override
    public void sendEmailVerificationWithToken(String to, String token) throws MessagingException, UnsupportedEncodingException {

    }

    @Override
    public void sendEmailVerificationWithCode(String to, String code) throws MessagingException, UnsupportedEncodingException {

    }

    @Override
    public void sendEmailToResetPassword(String to, String code) throws MessagingException, UnsupportedEncodingException {

    }
}
