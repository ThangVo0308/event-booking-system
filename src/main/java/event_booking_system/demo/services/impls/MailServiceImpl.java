package event_booking_system.demo.services.impls;

import event_booking_system.demo.components.Translator;
import event_booking_system.demo.services.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static event_booking_system.demo.components.Translator.getLocalizedMessage;

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
        sendMail(to,
                "subject_verify_email",
                "content_verify_email_with_token",
                "sub_content_verify_email",
                "footer_verify_email", String.format("""
                        http://localhost:%s/sports-field-booking/api/v1/auth/verify-email-by-token?token=%s""", port, token));
    }

    @Override
    public void sendEmailVerificationWithCode(String to, String code) throws MessagingException, UnsupportedEncodingException {
        sendMail(to,
                "subject_verify_email",
                "content_verify_email_with_code",
                "sub_content_verify_email",
                "footer_verify_email", code);
    }

    @Override
    public void sendEmailToResetPassword(String to, String code) throws MessagingException, UnsupportedEncodingException {
        sendMail(to,
                "subject_reset_password",
                "content_reset_password",
                "sub_reset_password",
                "footer_reset_password", code);
    }

    private void sendMail(String toMail, String subjectKey, String contentKey, String subContentKey, String footerKey, String secretCode) throws MessagingException, UnsupportedEncodingException {
        //subjectKey: í18n for mail language bases on region
        // contentKey: main content of the email
        // subContentKey: sub content of the email
        // footerKey: footer content of the email
        // secretCode: code for confirmation
        String subject = getLocalizedMessage(subjectKey);
        String contents = Arrays.toString(new String[] {
                getLocalizedMessage(contentKey),
                getLocalizedMessage(subContentKey)
        });
        String footer = getLocalizedMessage(footerKey);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(from, "Event Booking System");
        helper.setTo(toMail);
        helper.setSubject(subject);

        mailSender.send(message);
        log.info("Sent to: {}", toMail);
    }
}
