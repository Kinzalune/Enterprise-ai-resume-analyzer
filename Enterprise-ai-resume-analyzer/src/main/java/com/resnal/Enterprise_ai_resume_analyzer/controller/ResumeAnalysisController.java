package com.resnal.Enterprise_ai_resume_analyzer.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.resnal.Enterprise_ai_resume_analyzer.dto.ResumeAnalysisResponse;
import com.resnal.Enterprise_ai_resume_analyzer.service.PdfExtractionService;
import com.resnal.Enterprise_ai_resume_analyzer.service.PiRedactionService;
import com.resnal.Enterprise_ai_resume_analyzer.service.ResumeAnalysisService;
import com.resnal.Enterprise_ai_resume_analyzer.service.ResumeStorageService;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeAnalysisController {

    private final PdfExtractionService pdfExtractionService;
    private final PiRedactionService piiRedactionService;
    private final ResumeAnalysisService resumeAnalysisService;
    private final ResumeStorageService resumeStorageService;

    public ResumeAnalysisController(
            PdfExtractionService pdfExtractionService,
            PiRedactionService piiRedactionService,
            ResumeAnalysisService resumeAnalysisService,
            ResumeStorageService resumeStorageService) {
        this.pdfExtractionService = pdfExtractionService;
        this.piiRedactionService = piiRedactionService;
        this.resumeAnalysisService = resumeAnalysisService;
        this.resumeStorageService = resumeStorageService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<ResumeAnalysisResponse> analyzeResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription) throws IOException {

        // 1. Archive original resume to Google Cloud Storage (safe fallback)
        try {
            String gcsUri = resumeStorageService.uploadResume(file);
            System.out.println("Archived resume successfully to GCS: " + gcsUri);
        } catch (Exception e) {
            System.err.println("GCS Upload skipped or failed: " + e.getMessage());
        }

        // 2. Extract raw text from PDF
        String rawText = pdfExtractionService.extractText(file);

        // 3. Scrub PII (emails, phones) for governance and privacy
        String sanitizedText = piiRedactionService.redactPii(rawText);

        // 4. Request structured analysis from Gemini 2.0 Flash
        ResumeAnalysisResponse analysis = resumeAnalysisService.analyzeResume(sanitizedText, jobDescription);

        return ResponseEntity.ok(analysis);
    }
}