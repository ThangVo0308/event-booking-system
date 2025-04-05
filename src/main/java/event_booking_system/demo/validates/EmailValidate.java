package event_booking_system.demo.validates;

import java.util.regex.Pattern;

public class EmailValidate {
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private final Pattern emailPattern = Pattern.compile(EMAIL_REGEX);

    public boolean isInvalidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true;
        }
        return !emailPattern.matcher(email.trim()).matches();
    }
}
