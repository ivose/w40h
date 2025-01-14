package com.example.some.util.formatters;

public final class StringFormatter {
    private StringFormatter() {}

    public static String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        try {
            return org.apache.commons.text.StringEscapeUtils.escapeHtml4(input);
        } catch (Exception e) {
            // Fallback to basic sanitization if StringEscapeUtils fails
            return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
        }
    }

    public static String slugify(String input) {
        if (input == null) {
            return null;
        }
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}