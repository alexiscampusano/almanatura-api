package com.almanatura.api.validation;

/**
 * Single source of truth for internal-user plain passwords (login, bootstrap admin, future
 * create-user flows). Keeps rules explicit and testable without duplicating regex across the
 * codebase.
 */
public final class InternalPasswordPolicy {

    public static final int MIN_LENGTH = 12;
    public static final int MAX_LENGTH = 100;

    /**
     * Human-readable requirement line for validation messages, API docs, and ops runbooks.
     * Characters outside letters, digits, and this set are rejected to avoid whitespace and
     * ambiguous Unicode in passwords at this layer.
     */
    public static final String ALLOWED_SPECIALS = "!@#$%^&*()_+-=[]{}|;:,.?";

    public static final String REQUIREMENTS_MESSAGE =
            "Password must be "
                    + MIN_LENGTH
                    + "-"
                    + MAX_LENGTH
                    + " characters and include at least one lowercase letter, one uppercase letter,"
                    + " one digit, and one special character from: "
                    + ALLOWED_SPECIALS;

    private InternalPasswordPolicy() {}

    public static boolean isValid(String password) {
        return violationMessage(password) == null;
    }

    /**
     * @return {@code null} if valid, otherwise a short English reason for {@link
     *     jakarta.validation.ConstraintValidator} or logging.
     */
    public static String violationMessage(String password) {
        if (password == null) {
            return "password is null";
        }
        int len = password.length();
        if (len < MIN_LENGTH || len > MAX_LENGTH) {
            return "length must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters";
        }
        if (!password.chars().allMatch(InternalPasswordPolicy::isAllowedCodePoint)) {
            return "must contain only ASCII letters, digits, and allowed special characters";
        }
        boolean lower = false;
        boolean upper = false;
        boolean digit = false;
        boolean special = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isLowerCase(c)) {
                lower = true;
            } else if (Character.isUpperCase(c)) {
                upper = true;
            } else if (Character.isDigit(c)) {
                digit = true;
            } else if (ALLOWED_SPECIALS.indexOf(c) >= 0) {
                special = true;
            }
        }
        if (!lower) {
            return "must contain at least one lowercase letter";
        }
        if (!upper) {
            return "must contain at least one uppercase letter";
        }
        if (!digit) {
            return "must contain at least one digit";
        }
        if (!special) {
            return "must contain at least one allowed special character";
        }
        return null;
    }

    public static void validateOrThrow(String password) {
        String msg = violationMessage(password);
        if (msg != null) {
            throw new IllegalArgumentException(msg);
        }
    }

    private static boolean isAllowedCodePoint(int cp) {
        if (cp > 0x7F || cp < 0x20) {
            return false;
        }
        char c = (char) cp;
        if (Character.isLetter(c) && Character.isAlphabetic(cp)) {
            return true;
        }
        if (Character.isDigit(c)) {
            return true;
        }
        return ALLOWED_SPECIALS.indexOf(c) >= 0;
    }
}
