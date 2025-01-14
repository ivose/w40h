package com.example.some.util.constants;

public final class ValidationConstants {
    private ValidationConstants() {}

    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final int CONTENT_MAX_LENGTH = 5000;
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9._-]{3,50}$";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
}