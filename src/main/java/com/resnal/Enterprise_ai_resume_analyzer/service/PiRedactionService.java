package com.resnal.Enterprise_ai_resume_analyzer.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class PiRedactionService {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("(\\+\\d{1,3}[- ]?)?\\(?\\d{3}\\)?[-. ]?\\d{3}[-. ]?\\d{4}");

    public String redactSensitiveData(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String sanitized = EMAIL_PATTERN.matcher(input).replaceAll("[REDACTED_EMAIL]");
        return PHONE_PATTERN.matcher(sanitized).replaceAll("[REDACTED_PHONE]");
    }
}