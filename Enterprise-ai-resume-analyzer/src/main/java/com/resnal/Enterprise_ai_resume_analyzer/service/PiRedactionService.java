package com.resnal.Enterprise_ai_resume_analyzer.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class PiRedactionService {

    // Regex patterns for sensitive information
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("\\b(?:\\+?\\d{1,3}[- .]?)?\\(?\\d{3}\\)?[- .]?\\d{3}[- .]?\\d{4}\\b");

    /**
     * Scrubs PII from raw resume text before sending it to Vertex AI.
     */
    public String redactPii(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return "";
        }

        // Mask Emails
        String sanitizedText = EMAIL_PATTERN.matcher(rawText).replaceAll("[REDACTED_EMAIL]");

        // Mask Phone Numbers
        sanitizedText = PHONE_PATTERN.matcher(sanitizedText).replaceAll("[REDACTED_PHONE]");

        return sanitizedText;
    }
}