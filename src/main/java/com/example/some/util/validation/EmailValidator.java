package com.example.some.util.validation;

import java.util.regex.Pattern;
import com.example.some.util.constants.ValidationConstants;

public final class EmailValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(ValidationConstants.EMAIL_PATTERN);

    private EmailValidator() {}

    public static boolean isValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidLength(String email) {
        return email != null && email.length() <= ValidationConstants.EMAIL_MAX_LENGTH;
    }
}