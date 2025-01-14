package com.example.some.util.constants;

public final class SecurityConstants {
    private SecurityConstants() {}

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/users/signup";
    public static final String LOGIN_URL = "/api/users/login";
    public static final long TOKEN_EXPIRATION = 864_000_000; // 10 days
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 100;
}