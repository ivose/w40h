package com.example.some.util.security;

import java.security.SecureRandom;
import java.util.Base64;

public final class TokenUtils {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder();

    private TokenUtils() {}

    public static String generateToken() {
        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        return ENCODER.encodeToString(randomBytes);
    }

    public static String generateResetToken() {
        byte[] randomBytes = new byte[24];
        RANDOM.nextBytes(randomBytes);
        return ENCODER.encodeToString(randomBytes);
    }
}