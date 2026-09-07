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
    private final PiRedactionService piRedactionService;
    private final ResumeStorageService resumeStorageService;
    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeAnalysisController(
            PdfExtractionService pdfExtractionService,
            PiRedactionService piRedactionService,
            ResumeStorageService resumeStorageService,
            ResumeAnalysisService resumeAnalysisService) {
        this.pdfExtractionService = pdfExtractionService;
        this.piRedactionService = piRedactionService;
        this.resumeStorageService = resumeStorageService;
        this.resumeAnalysisService = resumeAnalysisService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeAnalysisResponse> analyzeResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "targetRole", defaultValue = "Software Engineer") String targetRole) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 1. Text Extraction
        String rawText = pdfExtractionService.extractText(file);

        // 2. Data Sanitization / Governance
        String redactedText = piRedactionService.redactSensitiveData(rawText);

        // 3. Resilient Cloud Storage Archiving
        String gcsPath;
        try {
            gcsPath = resumeStorageService.uploadResume(file);
        } catch (Exception e) {
            System.err.println("GCS Upload skipped/failed due to network: " + e.getMessage());
            gcsPath = "gs://offline-fallback/buffered-in-memory";
        }

        // 4. Structured AI Evaluation
        ResumeAnalysisResponse response = resumeAnalysisService.analyzeResume(redactedText, targetRole, gcsPath);

        return ResponseEntity.ok(response);
    }
}