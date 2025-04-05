package event_booking_system.demo.validates;

import java.util.regex.Pattern;

public class PasswordValidator {
    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private final Pattern passwordPattern = Pattern.compile(PASSWORD_REGEX);

    public boolean isWeakPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return true;
        }
        return !passwordPattern.matcher(password.trim()).matches();
    }
}
